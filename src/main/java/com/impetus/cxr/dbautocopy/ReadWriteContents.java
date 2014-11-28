package com.impetus.cxr.dbautocopy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class ReadWriteContents
{
  private static Logger logger = Logger.getLogger(ReadWriteContents.class);
  private static String readMeFile = getReadMeFileName();

  /**
   * Method to read all the folders
   * 
   * @return
   */
  public boolean processReadWriteOperation()
  {
    boolean successCopy = false;
    try
    {
      File file = new File(PropertyManager.CONNECT_DBA_MACHINE_FOR_SCRIPTS);
      if (file.exists())
      {
        File[] files = file.listFiles();
        List<String> foldersList = new ArrayList<String>();
        for (File folder : files)
        {
          logger.info("Folder Name : " + folder.getName());
          readCreateFolder(folder, foldersList);
        }
        if (foldersList != null && foldersList.size() > 0)
        {
          reporting(foldersList);
          successCopy = true;
        }
      }
    }
    catch (Exception e)
    {
      logger.error("Exception in ReadWriteContents.processReadWriteOperation method: ", e);
    }
    return successCopy;
  }

  /**
   * Method to create list of project names which got updated in txt file
   * 
   * @param folders
   * @throws IOException
   */
  public void reporting(List<String> folders) throws IOException
  {
    ReadWriteContents contents = new ReadWriteContents();
    String filePath = PropertyManager.PATH_OF_REPORTING;
    File f1 = new File(filePath);
    if (!f1.exists())
    {
      f1.mkdirs();
    }
    String date = contents.getDateForFile();
   
    File file = new File(filePath+ date);
    if (!file.exists())
    {
      file.createNewFile();
    }
    StringBuffer projectNames = new StringBuffer();
    for (String folder : folders)
    {
      projectNames.append(folder);
      projectNames.append("\r\n");
    }

    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write((projectNames.toString()).getBytes());
    fileOutputStream.flush();
    fileOutputStream.close();

  }

  /**
   * Method to read the script file from the folder
   * 
   * @param folder
   * @param foldersList
   * @throws Exception
   */
  private void readCreateFolder(File folder, List<String> foldersList) throws Exception
  {
    CopyFileToDestination copyFileToDestination = new CopyFileToDestination();
    List<String> sqlFiles = new ArrayList<String>();
    List<String> bakFiles = new ArrayList<String>();
    FileFilter filter = new FileFilter()
      {
        @Override
        public boolean accept(File pathname)
        {
          return pathname.getName().equals(readMeFile);
        }
      };

    File[] folderFiles = folder.listFiles(filter);

    for (File folderFile : folderFiles)
    {
      if (folderFile.isFile() && folderFile.getName().equals(readMeFile))
      {
        logger.info("Selected File Name : " + folderFile.getName());
        BufferedReader reader = new BufferedReader(new FileReader(folderFile));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null)
        {
          stringBuilder.append(line);
          stringBuilder.append(" ");
        }
        String content = stringBuilder.toString().trim();
        if (content.length() > 0)
        {
          File destFolder = createFolder(folder.getName());

          copyFileToDestination.copyReadMe(destFolder, folderFile);
          String[] contentArray = content.split(" ");
          for (String text : contentArray)
          {
            if (text.endsWith(".sql") || text.endsWith(".exe") || text.endsWith(".zip")
              || text.endsWith(".bat"))
            {
              sqlFiles.add(text);
            }
         /* else if (text.endsWith(".bak") || text.endsWith(".zip") || text.endsWith(".exe")
              || text.endsWith(".bat"))
            {
              bakFiles.add(text);
            }*/
          }
          if (sqlFiles.size() > 0)
          {
            copyFileToDestination.processReadWriteOperationForScript(destFolder, folder, sqlFiles);
            foldersList.add(folder.getName());
          }
          /* if (bakFiles.size() > 0)
          {
            copyFileToDestination.copyBackupFiles(destFolder, bakFiles, folder);
            foldersList.add(folder.getName());
          }*/
        
      }
    }}}
  

  /**
   * Method to get the script file from DBA Machine
   * 
   * @return
   */
  public static String getReadMeFileName()
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Calendar todaysDate = Calendar.getInstance();
 
      todaysDate.add(Calendar.DATE, -1);
    
    String prevDate = format.format(todaysDate.getTime());
    return "readme_" + prevDate + ".txt";
  }

  /**
   * Method to create the log file which consist of updated folders
   * 
   * @return
   */
  public static String getDateForFile()
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Calendar todaysDate = Calendar.getInstance();
   
    String prevDate = format.format(todaysDate.getTime());
    return "List_Of_Updated_Folders_For_" + prevDate + "_txt.log";
  }

  /**
   * Method to create the folder, if it does not exist
   * 
   * @param folderName
   * @return
   */
  public File createFolder(String folderName)
  {

    String filePath = PropertyManager.PATH_OF_FOLDER + folderName + "/";
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
