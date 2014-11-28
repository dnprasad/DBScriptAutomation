package com.impetus.cxr.dbautocopy;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class CopyFileToDestination
{
  public static final String DEFAULT_SEPARATOR = ",";
  private static Logger logger = Logger.getLogger(CopyFileToDestination.class);

  public void copyReadMe(File destFolder, File readMeFile) throws Exception
  {
    FileUtils.copyFileToDirectory(readMeFile, destFolder);
  }

  /**
   * Method to copy the script to destination
   * 
   * @param destFolder
   * @param projectFolder
   * @param sqlFiles
   * @throws IOException
   */
  public void processReadWriteOperationForScript(File destFolder, File projectFolder,
      final List<String> sqlFiles) throws IOException
  {
    FileFilter filter = new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isDirectory() && pathname.getName().equals("Scripts");
      }
    };
    FileFilter sqlFilter = new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile() && sqlFiles.contains(pathname.getName());
      }
    };
    File[] scriptFolders = projectFolder.listFiles(filter);
    for (File scriptFolder : scriptFolders)
    {
      File[] sqlFilesToRead = scriptFolder.listFiles(sqlFilter);
      for (File sqlFile : sqlFilesToRead)
      {
        if (sqlFile.exists())
        {
          logger.info("Copying the file to the destination");
          FileUtils.copyFileToDirectory(sqlFile, destFolder);
        }
      }
    }
  }

  /**
   * Method to connect to FTPClient
   * 
   * @return
   */
  private FTPClient connectFTPClient()
  {
    FTPClient ftpClient = new FTPClient();
    ftpClient.setDefaultTimeout(18000000);
    try
    {
      ftpClient.connect(PropertyManager.CONNECT_DBA_MACHINE_FOR_BAK_UP_FILES);
      ftpClient.login(PropertyManager.USERNAME_DBA_MACHINE_FOR_BAK_UP_FILES,
          PropertyManager.PASSWORD_DBA_MACHINE_FOR_BAK_UP_FILES);
      ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
      ftpClient.enterLocalPassiveMode();
    }
    catch (SocketException e)
    {
      logger.error("SocketException in CopyFileToDestination.connectFTPClient method: ", e);
    }
    catch (IOException e)
    {
      logger.error("IOException in CopyFileToDestination.connectFTPClient method: ", e);
    }
    return ftpClient;
  }

  /**
   * Method to copy the bakup to destination
   * 
   * @param destFolder
   * @param backList
   * @param projectFolder
   */
  public void copyBackupFiles(File destFolder, List<String> backList, File projectFolder)
  {
    FTPClient ftpClient = connectFTPClient();
    int reply = 0;
    try
    {
      reply = ftpClient.getReplyCode();
      if (FTPReply.isPositiveCompletion(reply))
      {
        for (String backFile : backList)
        {
          InputStream in = ftpClient.retrieveFileStream(backFile);
          byte[] bytesArray = new byte[4096];
          int bytesRead = -1;
          File file = new File(destFolder.getPath() + "\\" + backFile);
          if (!file.exists())
          {
            logger.info("Copying the back up file to the destination");
            file.createNewFile();
          }
          OutputStream out = new FileOutputStream(file);
          while ((bytesRead = in.read(bytesArray)) != -1)
          {
            out.write(bytesArray, 0, bytesRead);
          }
          boolean success = ftpClient.completePendingCommand();
          out.close();
          in.close();
        }
      }
    }
    catch (SocketException e)
    {
      logger.error("SocketException in CopyFileToDestination.copyBackupFiles method: ", e);
    }
    catch (IOException e)
    {
      logger.error("IOException in CopyFileToDestination.copyBackupFiles method: ", e);
    }
    finally
    {
      try
      {
        if (ftpClient != null && ftpClient.isConnected())
        {
          ftpClient.disconnect();
        }
      }
      catch (IOException e)
      {
        logger.error("IOException in CopyFileToDestination.copyBackupFiles method: ", e);
      }
    }
  }
}
