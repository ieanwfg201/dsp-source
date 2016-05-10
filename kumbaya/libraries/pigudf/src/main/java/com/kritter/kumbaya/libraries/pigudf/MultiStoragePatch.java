/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the
 * NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.kritter.kumbaya.libraries.pigudf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.pig.StoreFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.util.StorageUtil;

/**
 * This UDF splits the output data into multiple directories
 * dynamically based on user specified key field(s) in the output tuple.
 * <p>
 * <b>Sample usage:</b>
 * <p>
 * Consider the following input datafile 'mydata':
 * <p>
 * <blockquote><code>
 * a1   b1   1234<br>
 * a1   b2   2345<br>
 * a2   b1   3456<br>
 * a2   b2   4567<br>
 * ...<br>
 * </code></blockquote>
 * <p>
 * And the following PigLatin script:<br>
 * <p>
 * <blockquote><code>
 * A = LOAD 'mydata' USING PigStorage() as (a, b, c);<br>
 * STORE A INTO '/my/home/output' USING MultiStoragePatch('/my/home/output','0', 'none', '\\t');<br>
 * </code></blockquote>
 * <p>
 * The resulting output file directory structure would be as follows:
 * <p><blockquote>
 * /my/home/output/a1/a1-0000<br>
 * /my/home/output/a1/a1-0001<br>
 * /my/home/output/a2/a2-0000<br>
 * /my/home/output/a2/a2-0001<br>
 * </blockquote><p>
 * The suffix '0000*' is the task-id of the mapper/reducer task executing this
 * store. In case user does a GROUP BY on the field followed by MultiStoragePatch(),
 * then its imperative that all tuples for a particular group will go exactly to
 * 1 reducer. So in the above case for e.g. there will be only 1 file each under
 * 'a1' and 'a2' directories.
 * <p>
 * If compression is specified, the sub directories and the output files will
 * have the appropriate extension. For example, if the above case specified 'bz2'
 * as the compression type instead of 'none', the output files would be as follows:
 * <p><blockquote>
 * /my/home/output/a1.bz2/a1-0000.bz2<br>
 * /my/home/output/a1.bz2/a1-0001.bz2<br>
 * /my/home/output/a2.bz2/a2-0000.bz2<br>
 * /my/home/output/a2.bz2/a2-0001.bz2<br>
 * </blockquote><p>
 * MultiStoragePatch now supports using multiple indexes from each tuple to build
 * the directory structure.  For example, given the above input datafile 'mydata',
 * the splitFieldIndex parameter may be specified as '0/1'.  This would mean the
 * first level subdirectory will be based on the value of the field at index 0, 
 * the second level subdirectory will be based on the value of the field at index 1,
 * and the resulting output structure would be as follows:
 * <p><blockquote>
 * /my/home/output/a1/b1/a1-b1-0000<br>
 * /my/home/output/a1/b2/a1-b2-0000<br>
 * /my/home/output/a2/b1/a2-b1-0000<br>
 * /my/home/output/a2/b2/a2-b2-0000<br>
 * </blockquote><p>
 * There is currently no enforced limit to the number of indexes that can be specified
 * (though any given index value must of course be within the number of fields available
 * in the tuple), nor must they be consecutive within the tuple or presented in any
 * particular order. The order given is the order used to generate the resulting
 * directory structure.
 */
public class MultiStoragePatch extends StoreFunc {

  private Path outputPath; // User specified output Path
  private int[] splitFieldIndices; // Index(es) of the key field(s)
  private String fieldDel; // delimiter of the output record.
  private Compression comp; // Compression type of output data.
  
  // Compression types supported by this store
  enum Compression {
    none, bz2, bz, gz;
  };

  public MultiStoragePatch(String parentPathStr, String splitFieldIndex) {
    this(parentPathStr, splitFieldIndex, "none");
  }

  public MultiStoragePatch(String parentPathStr, String splitFieldIndex,
      String compression) {
    this(parentPathStr, splitFieldIndex, compression, "\\t");
  }

  /**
   * Constructor
   * 
   * @param parentPathStr   The DFS path where output directories and files will be created.
   * @param splitFieldIndex Index of field(s) whose values should be used to create directories and files.
   *                        See class comments above for details.
   * @param compression     The compression type to use.  Default is 'none'; supported types are 'none', 'gz', 'bz', and 'bz2'
   * @param fieldDelim      Output record field separator
   */
  public MultiStoragePatch(String parentPathStr, String splitFieldIndex,
      String compression, String fieldDelim) {
    this.outputPath = new Path(parentPathStr);
    String[] splitFieldArr = splitFieldIndex.split("/");
    this.splitFieldIndices = new int[splitFieldArr.length];
    for (int idx = 0; idx < this.splitFieldIndices.length; idx++) {
        this.splitFieldIndices[idx] = Integer.parseInt(splitFieldArr[idx]);
    }
    this.fieldDel = fieldDelim;
    try {
      this.comp = (compression == null) ? Compression.none : Compression
        .valueOf(compression.toLowerCase());
    } catch (IllegalArgumentException e) {
      System.err.println("Exception when converting compression string: "
          + compression + " to enum. No compression will be used");
      this.comp = Compression.none;
    }
  }

  //--------------------------------------------------------------------------
  // Implementation of StoreFunc

  private RecordWriter<String, Tuple> writer;
  
  @Override
  public void putNext(Tuple tuple) throws IOException {
    StringBuilder buf = new StringBuilder();
      for (int idx = 0; idx < this.splitFieldIndices.length; idx++) {
        if (tuple.size() <= this.splitFieldIndices[idx]) {
          throw new IOException("split field index:" + this.splitFieldIndices[idx]
              + " >= tuple size:" + tuple.size());
        }
        Object field = null;
        try {
          field = tuple.get(this.splitFieldIndices[idx]);
          buf.append(field);
          if (idx < this.splitFieldIndices.length - 1) {
              buf.append(Path.SEPARATOR);
          }
        } catch (ExecException exec) {
          throw new IOException(exec);
        }
    }
    try {
      writer.write(buf.toString(), tuple);
    } catch (InterruptedException e) {
      throw new IOException(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public OutputFormat getOutputFormat() throws IOException {
      MultiStoragePatchOutputFormat format = new MultiStoragePatchOutputFormat();
      format.setKeyValueSeparator(fieldDel);
      return format;
  }
    
  @SuppressWarnings("unchecked")
  @Override
  public void prepareToWrite(RecordWriter writer) throws IOException {
      this.writer = writer;
  }
    
  @Override
  public void setStoreLocation(String location, Job job) throws IOException {
    job.getConfiguration().set("mapred.textoutputformat.separator", "");
    FileOutputFormat.setOutputPath(job, new Path(location));
    if (comp == Compression.bz2 || comp == Compression.bz) {
      FileOutputFormat.setCompressOutput(job, true);
      FileOutputFormat.setOutputCompressorClass(job,  BZip2Codec.class);
    } else if (comp == Compression.gz) {
      FileOutputFormat.setCompressOutput(job, true);
      FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
    }
  }
 
  //--------------------------------------------------------------------------
  // Implementation of OutputFormat
  
  public static class MultiStoragePatchOutputFormat extends
  TextOutputFormat<String, Tuple> {

    private String keyValueSeparator = "\\t";
    private byte fieldDel = '\t';
  
    @Override
    public RecordWriter<String, Tuple> 
    getRecordWriter(TaskAttemptContext context
                ) throws IOException, InterruptedException {
    
      final TaskAttemptContext ctx = context;
        
      return new RecordWriter<String, Tuple>() {

        private Map<String, MyLineRecordWriter> storeMap = 
              new HashMap<String, MyLineRecordWriter>();
          
        private static final int BUFFER_SIZE = 1024;
          
        private ByteArrayOutputStream mOut = 
              new ByteArrayOutputStream(BUFFER_SIZE);
                           
        @Override
        public void write(String key, Tuple val) throws IOException {                
          int sz = val.size();
          for (int i = 0; i < sz; i++) {
            Object field;
            try {
              field = val.get(i);
            } catch (ExecException ee) {
              throw ee;
            }

            StorageUtil.putField(mOut, field);

            if (i != sz - 1) {
              mOut.write(fieldDel);
            }
          }
              
          getStore(key).write(null, new Text(mOut.toByteArray()));

          mOut.reset();
        }

        @Override
        public void close(TaskAttemptContext context) throws IOException { 
          for (MyLineRecordWriter out : storeMap.values()) {
            out.close(context);
          }
        }
      
        private MyLineRecordWriter getStore(String fieldValue) throws IOException {
          MyLineRecordWriter store = storeMap.get(fieldValue);
          if (store == null) {                  
            DataOutputStream os = createOutputStream(fieldValue);
            store = new MyLineRecordWriter(os, keyValueSeparator);
            storeMap.put(fieldValue, store);
          }
          return store;
        }
          
        private DataOutputStream createOutputStream(String fieldValue) throws IOException {
          Configuration conf = ctx.getConfiguration();
          TaskID taskId = ctx.getTaskAttemptID().getTaskID();
          
          // Check whether compression is enabled, if so get the extension and add them to the path
          boolean isCompressed = getCompressOutput(ctx);
          CompressionCodec codec = null;
          String extension = "";
          if (isCompressed) {
             Class<? extends CompressionCodec> codecClass = 
                getOutputCompressorClass(ctx, GzipCodec.class);
             codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, ctx.getConfiguration());
             extension = codec.getDefaultExtension();
          }

          // need to make sure the taskId is zero padded and therefore sortable
          NumberFormat nf = NumberFormat.getInstance();
          nf.setMinimumIntegerDigits(4);
          nf.setGroupingUsed(false);

          Path path = new Path(fieldValue+extension, fieldValue.replace('/', '-') + '-'
                + nf.format(taskId.getId())+extension);
          Path workOutputPath = ((FileOutputCommitter)getOutputCommitter(ctx)).getWorkPath();
          Path file = new Path(workOutputPath, path);
          FileSystem fs = file.getFileSystem(conf);                
          FSDataOutputStream fileOut = fs.create(file, false);
          
          if (isCompressed)
             return new DataOutputStream(codec.createOutputStream(fileOut));
          else
             return fileOut;
        }
          
      };
    }
  
    public void setKeyValueSeparator(String sep) {
      keyValueSeparator = sep;
      fieldDel = StorageUtil.parseFieldDel(keyValueSeparator);  
    }
  
  //------------------------------------------------------------------------
  //
  
    protected static class MyLineRecordWriter
    extends TextOutputFormat.LineRecordWriter<WritableComparable, Text> {

      public MyLineRecordWriter(DataOutputStream out, String keyValueSeparator) {
        super(out, keyValueSeparator);
      }
    }
  }

}
