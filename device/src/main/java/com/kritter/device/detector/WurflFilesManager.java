package com.kritter.device.detector;

import com.kritter.device.util.DeviceUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import lombok.Getter;
import net.sourceforge.wurfl.core.GeneralWURFLEngine;
import net.sourceforge.wurfl.core.WURFLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class keeps updating the wurfl files list present in the provided
 * wurfl data directory, it does so periodically at a given frequency
 * provided by HandsetDetector, at the end of finding out all the data
 * files it loads up the WurflEngine required for detection.
 */
public class WurflFilesManager
{
    private Logger logger;
    @Getter
    private WURFLEngine wurflEngine;
    private String wurflFilesDirectory;
    @Getter
    private int latestWurflFileVersion;
    @Getter
    private String latestWurflFileFullPath;
    private Timer filesLookupAndWurflEngineReloadTimer;
    private TimerTask filesLookupAndWurflEngineReloadTimerTask;

    public WurflFilesManager(String loggerName,String wurflFilesDirectory,long reloadFrequency) throws Exception
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.wurflFilesDirectory = wurflFilesDirectory;

        /*initially lookup all wurfl files once.*/
        loadLatestVersionValueAndCorrespondingFilePathAndItsWurflEngine();

        this.filesLookupAndWurflEngineReloadTimer = new Timer();
        this.filesLookupAndWurflEngineReloadTimerTask = new WurflFilesLookupAndWurflEngineReloadTimerTask();
        this.filesLookupAndWurflEngineReloadTimer.
                schedule(this.filesLookupAndWurflEngineReloadTimerTask,
                         reloadFrequency,
                         reloadFrequency);
    }

    private void loadLatestVersionValueAndCorrespondingFilePathAndItsWurflEngine() throws Exception
    {
        File wurflFilesFolder = new File(wurflFilesDirectory);

        for(File prospectiveWurflFile : wurflFilesFolder.listFiles())
        {
            logger.debug("Inside WurflFilesManager , file being looked up for wurfl latest version loading is : {}",
                         prospectiveWurflFile.getPath());

            //check if the current wurfl file is processed file or not.
            if(!prospectiveWurflFile.getName().endsWith(ApplicationGeneralUtils.PROCESSED_WURFL_FILE_EXTENSION))
            {
                logger.debug("The wurfl file : {} is not yet processed by the master node " +
                             "HandsetDataPopulator, skipping...",prospectiveWurflFile.getPath());
                continue;
            }

            //even if multiple files are present, which is expected, if any of the file's name
            //format is wrong, then throw exception, so that there is no confusion.
            int wurflFileVersion = DeviceUtils.verifyWurflFileNameAndFetchVersion(prospectiveWurflFile.getName());

            if(latestWurflFileVersion <= wurflFileVersion)
            {
                this.latestWurflFileVersion  = wurflFileVersion;
                this.latestWurflFileFullPath = prospectiveWurflFile.getPath();
            }
        }

        logger.error("NO ERROR !!! , Important Info: loaded latest wurflFile as: {} ", this.latestWurflFileFullPath);

        if(null != this.latestWurflFileFullPath)
            this.wurflEngine = new GeneralWURFLEngine(this.latestWurflFileFullPath);
    }

    private class WurflFilesLookupAndWurflEngineReloadTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            try
            {
                loadLatestVersionValueAndCorrespondingFilePathAndItsWurflEngine();
            }
            catch (Exception e)
            {
                logger.error("Exception inside timer task in reloading latest wurfl files inside WurflFilesManager",e);
            }
        }
    }

    public void releaseResources()
    {
        if(null != this.filesLookupAndWurflEngineReloadTimerTask &&
           null != this.filesLookupAndWurflEngineReloadTimer)
        {
            this.filesLookupAndWurflEngineReloadTimerTask.cancel();
            this.filesLookupAndWurflEngineReloadTimer.cancel();
            this.filesLookupAndWurflEngineReloadTimer.purge();
        }
    }
}