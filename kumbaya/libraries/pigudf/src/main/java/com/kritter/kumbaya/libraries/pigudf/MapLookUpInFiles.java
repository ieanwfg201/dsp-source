package com.kritter.kumbaya.libraries.pigudf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.hadoop.datastorage.ConfigurationUtil;
import org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigMapReduce;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.io.FileLocalizer;
import org.apache.pig.impl.logicalLayer.schema.Schema;

/**
* <dl>
* <dt><b>Syntax:</b></dt>
* <dd><code>int lookupInFiles(String expression,... <comma separated filelist>)</code>.</dd>
* <dt><b>Input:</b></dt>
* <dd><code>files are text files on DFS</code>.</dd>
* <dt><b>Output:</b></dt>
* <dd><code>if any file contains expression, return 1, otherwise, 0</code>.</dd>
* </dl>
*/

public class MapLookUpInFiles extends EvalFunc<String> {
    boolean initialized = false;
    ArrayList<String> mFiles = new ArrayList<String>();
    Map<String, String> mKeys = new HashMap<String, String>();
    static Map<ArrayList<String>, Map<String, String>> mTables = new HashMap<ArrayList<String>, Map<String, String>>(); 

    @Override
    public Schema outputSchema(Schema input) {
      try {
          return new Schema(new Schema.FieldSchema(getSchemaName(this.getClass().getName().toLowerCase(), input), DataType.INTEGER));
      } catch (Exception e) {
        return null;
      }
    }
    
    public void init(Tuple tuple) throws IOException {
        String delimiter = tuple.get(1).toString();
        for (int count = 2; count < tuple.size(); count++) {
            if (!(tuple.get(count) instanceof String)) {
                String msg = "MapLookUpInFiles : Filename should be a string.";
                throw new IOException(msg);
            }
            mFiles.add((String) tuple.get(count));
        }

        if (mTables.containsKey(mFiles))
        {

            mKeys = mTables.get(mFiles);
        }
        else
        {
            Properties props = ConfigurationUtil.toProperties(PigMapReduce.sJobConfInternal.get());
            for (int i = 0; i < mFiles.size(); ++i) {
                // Files contain only 1 column with the key. No Schema. All keys
                // separated by new line.
    
                BufferedReader reader = null;
    
                InputStream is = null;
                try {
                    is = FileLocalizer.openDFSFile(mFiles.get(i), props);
                } catch (IOException e) {
                    String msg = "MapLookUpInFiles : Cannot open file "+mFiles.get(i);
                    throw new IOException(msg, e);
                }
                try {
                    reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String lineSplit[] = line.split(delimiter);
                        if (!mKeys.containsKey(lineSplit[0])){
                            mKeys.put(lineSplit[0], lineSplit[1]);
                        }
                    }
                    is.close();
                } catch (IOException e) {
                    String msg = "MapLookUpInFiles : Cannot read file "+mFiles.get(i);
                    throw new IOException(msg, e);
                }
            }
            mTables.put(mFiles, mKeys);
        }
        initialized=true;
    }

    @Override
    public String exec(Tuple input) throws IOException {
        if (!initialized)
            init(input);
        if (input.get(0)==null)
            return null;
        String str = input.get(0).toString();
        if (mKeys.containsKey(str))
            return mKeys.get(str);
        return "-1";
    }
}
