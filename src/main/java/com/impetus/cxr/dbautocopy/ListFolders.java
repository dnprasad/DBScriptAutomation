package com.impetus.cxr.dbautocopy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class ListFolders
{
  private static Logger logger = Logger.getLogger(ListFolders.class);

  public static void main(String[] args) throws IOException
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    ReadWriteContents contents = new ReadWriteContents();
    boolean successCopyFromCoors = contents.processReadWriteOperation();
    Calendar todaysDate = Calendar.getInstance();
    if (successCopyFromCoors)
    {
      CopyUpdatedContent copyUpdatedContent = new CopyUpdatedContent();
      copyUpdatedContent.readFile();
    }
    else
    {
      try
      {
        String content = "No Changes could be found today";
        String prevDate = format.format(todaysDate.getTime());
        File file = new File(PropertyManager.PATH_OF_FOLDER + "/" + "List_Of_Updated_Folders_For_"
            + prevDate + "_txt.log");
        if (!file.exists())
        {
          file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
      }
      catch (IOException e)
      {
        logger.error("ListFolders in CopyFileToDestination.main method: ", e);
      }
    }
  }
}
