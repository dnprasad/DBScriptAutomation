package com.impetus.cxr.dbautocopy;

/**
 * A property manager for exemplar project wide properties
 */
public class PropertyManager
{
  /**
   * properties key for connecting to DBA machine for script files
   */
  public static final String CONNECT_DBA_MACHINE_FOR_SCRIPTS = "////10.1.2.1/development/dba/Remote";
  /**
   * properties key for copying the files
   */
  public static final String PATH_OF_FOLDER = "E:/Connecture/";
  /**
   * properties key for copying the files
   */
  public static final String PATH_OF_REPORTING = "E:/Connecture/ReportingFolder/";
  /**
   * properties key for connecting to DBA machine for backup files
   */
  public static final String CONNECT_DBA_MACHINE_FOR_BAK_UP_FILES = "10.2.1.209";
  /**
   * properties key for username
   */
  public static final String USERNAME_DBA_MACHINE_FOR_BAK_UP_FILES = "Impetus_FTP";
  /**
   * properties key for password
   */
  public static final String PASSWORD_DBA_MACHINE_FOR_BAK_UP_FILES = "HcJAfaAcQd";
  /**
   * properties key for connecting to Indore server
   */
  public static final String CONNECT_DBA_MACHINE_FOR_INDORE = "//172.26.32.226/Connecture";
}