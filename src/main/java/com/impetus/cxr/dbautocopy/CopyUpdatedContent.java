package com.impetus.cxr.dbautocopy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class CopyUpdatedContent
{
  private static Logger logger = Logger.getLogger(CopyUpdatedContent.class);

  /**
   * Method to read the log file which consists of updated folders list
   */
  public static void readFile()
  {
    ReadWriteContents contents = new ReadWriteContents();
    String date = contents.getDateForFile();
    String folderDate = getDateTime();
    File file = new File(PropertyManager.PATH_OF_REPORTING + date);
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = null;
      StringBuilder stringBuilder = new StringBuilder();
      while ((line = reader.readLine()) != null)
      {
        stringBuilder.append(line);
        stringBuilder.append("\n");
      }
      File fileConnection = new File(PropertyManager.CONNECT_DBA_MACHINE_FOR_INDORE);
      if (fileConnection.exists())
      {
        File[] files = fileConnection.listFiles();
        String[] strings = stringBuilder.toString().split("\n");
        for (String projectName : strings)
        {
          if (projectName != null)
          {
            File rootFolder = new File(PropertyManager.PATH_OF_FOLDER + "/" + projectName + "/"
                + folderDate);
            createFolder(projectName);
            File destFolder = new File(PropertyManager.CONNECT_DBA_MACHINE_FOR_INDORE + "/"
                + projectName + "/" + folderDate);
            FileUtils.copyDirectory(rootFolder, destFolder);
          }
        }
      }
    }
    catch (IOException e)
    {
      logger.error("Exception in CopyUpdatedContent.readFile() method: ", e);
    }
  }

  /**
   * Method to create the folder, if it does not exist
   * 
   * @param folderName
   * @return
   */
  public static File createFolder(String folderName)
  {
    String filePath = PropertyManager.CONNECT_DBA_MACHINE_FOR_INDORE + "/" + folderName + "/";
    String foldername = getDateTime();
    String finalPath = filePath + foldername;
    File f1 = new File(finalPath);
    if (!f1.exists())
    {
      f1.mkdirs();
    }
    return f1;
  }

  /**
   * Method to return date
   * 
   * @return
   */
  private final static String getDateTime()
  {
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    return df.format(new Date());
  }
}
