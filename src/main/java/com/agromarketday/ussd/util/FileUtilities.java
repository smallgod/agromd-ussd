package com.agromarketday.ussd.util;

import com.agromarketday.ussd.constant.ErrorCode;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 *
 * @author smallgod
 */
public class FileUtilities {

    private static final LoggerUtil logger = new LoggerUtil(FileUtilities.class);

    private static final int BUFFER_SIZE = 8 * 1024;

    /**
     *
     * @param inputFile
     * @param outputFile
     * @return true | false if file was converted successfulLy
     */
    public static boolean convertFileToCSV(String inputFile, String outputFile) {

        //String command = "ping -c 3 www.google.com";
        String createFile = "touch %s";
        String formattedCmd1 = String.format(createFile, outputFile);
        String conversionCmd = "ssconvert --export-type=Gnumeric_stf:stf_csv %s %s";
        String formattedCmd = String.format(conversionCmd, inputFile, outputFile);

        logger.debug("... executing command $ " + formattedCmd);

        Process proc;
        String line = "";
        int exitValue = 0;

        try {

            //execute the command
            Runtime.getRuntime().exec(formattedCmd1);
            proc = Runtime.getRuntime().exec(formattedCmd);

            //Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            while ((line = reader.readLine()) != null) {
                logger.debug(line + "\n");
            }

            exitValue = proc.waitFor();

        } catch (IOException | InterruptedException | SecurityException ex) {
            //throw new MyCustomExceptionOLD("Exception", ErrorCode.COMMUNICATION_ERR, "Exception executing shell command ->> $ " + formattedCmd + " ->> with errormsg: " + ex.getMessage(), ErrorCategory.SERVER_ERR_TYPE);

        } catch (Exception ex) {
            //throw new MyCustomExceptionOLD("Exception", ErrorCode.COMMUNICATION_ERR, "Exception executing shell command ->> $ " + formattedCmd + " ->> with errormsg: " + ex.getMessage(), ErrorCategory.SERVER_ERR_TYPE);

        }

        if (exitValue == 0X0) { //0 indicates normal termination
            return Boolean.TRUE;
        } else {

            //throw new MyCustomExceptionOLD("Abnormal termination", ErrorCode.INTERNAL_ERR, "Exception executing shell command ->> $ " + formattedCmd + " terminated with a non-normal flag:: " + exitValue, ErrorCategory.SERVER_ERR_TYPE);
            return Boolean.FALSE;
        }
    }

    /**
     * *
     * Get file extension e.g. txt, xls, ods, odt, csv, xlsl
     *
     * @param fileName
     * @return
     * @throws NullPointerException
     */
    public static String getFileExtension(String fileName) {

        String fileExtension = null;

        logger.info("fileName::: " + fileName);

        int indexOfDot = fileName.lastIndexOf('.');

        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (indexOfDot > p) {

            fileExtension = fileName.substring(indexOfDot + 1);
        }

        if (fileExtension == null) {
            //throw new MyCustomExceptionOLD("NullPointer Exception", ErrorCode.NOT_SUPPORTED_ERR, "Failed to get file Extension", ErrorCategory.CLIENT_ERR_TYPE);
        }

        logger.info("File Extension got: " + fileExtension);

        return fileExtension.trim();
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String removeFileExtension(String fileName) {

        String subFileName;
        if (fileName.indexOf(".") > 0) {
            subFileName = fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            subFileName = fileName;
        }

        return subFileName;
    }

    /**
     *
     * @param oldFileName
     * @param newFileExtension
     * @return filename with new extension
     * @throws com.namaraka.recon.exceptiontype.MyCustomException
     */
    public static String changeFileTypeToCSV(String oldFileName, String newFileExtension) {

        String newFileName = null;

        int indexOfDot = oldFileName.lastIndexOf('.');
        int p = Math.max(oldFileName.lastIndexOf('/'), oldFileName.lastIndexOf('\\'));

        if (indexOfDot > p) {
            newFileName = oldFileName.replaceFirst(oldFileName.substring(indexOfDot + 1), newFileExtension);
        }

        if (newFileName == null) {
            //throw new MyCustomExceptionOLD("NullPointer Exception", ErrorCode.NOT_SUPPORTED_ERR, "Failed to create new File Name from:: " + oldFileName, ErrorCategory.SERVER_ERR_TYPE);

        }
        return newFileName.trim();
    }

    /**
     *
     * @param absoluteFileName
     * @return fileNameAndType
     */
    public static String getFileNameAndType(String absoluteFileName) {

        int indexOfFileName = Math.max(absoluteFileName.lastIndexOf('/'), absoluteFileName.lastIndexOf('\\'));
        String fileNameAndType = absoluteFileName.substring(indexOfFileName + 1).trim();

        return fileNameAndType;
    }

    /**
     *
     * @param source
     * @param dest
     * @throws com.library.customexception.MyCustomException
     */
    public static void copyFile(File source, File dest) throws MyCustomException {

        try {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {

            String errorDescription = "Error! Failed to copy file";
            String errorDetails = "IO Error occurred while copying file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;
        }
    }

    /**
     *
     * @param source
     * @param dest
     * @return
     * @throws MyCustomException
     */
    public static boolean copyFile(String source, String dest) throws MyCustomException {

        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        boolean isCopied = Boolean.FALSE;

        try {

            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            long bitesTransfered = destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            isCopied = Boolean.TRUE;

        } catch (FileNotFoundException ex) {

            String errorDescription = "Error! Failed to copy file";
            String errorDetails = "FileNotFoundException Error occurred while copying file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;

        } catch (IOException ex) {

            String errorDescription = "Error! Failed to copy file";
            String errorDetails = "IO Error occurred while copying file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {

            try {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException ex) {
                logger.error("IOException occurred while closing openned File Streams: " + ex.getMessage());
            }
        }

        return isCopied;
    }

    /**
     *
     * @param source
     * @param dest
     * @throws MyCustomException
     */
    public static void copyFileUsingChannel(File source, File dest) throws MyCustomException {

        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            long bitesTransfered = destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        } catch (FileNotFoundException ex) {

            String errorDescription = "Error! Failed to copy file";
            String errorDetails = "FileNotFoundException Error occurred while copying file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;

        } catch (IOException ex) {

            String errorDescription = "Error! Failed to copy file";
            String errorDetails = "IO Error occurred while copying file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {
            try {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException ex) {
                logger.error("IOException occurred while closing openned File Streams: " + ex.getMessage());
            }
        }
    }

    public static void copyEntireFolderNEEDSAPACHE(File sourceDir, File destDir) {

//        try {
//            FileUtils.copyDirectory(sourceDir, destDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * This function recursively copy all the sub folder and files from
     * sourceFolder to destinationFolder
     *
     * @param sourceFolder
     * @param destinationFolder
     * @throws com.library.customexception.MyCustomException
     */
    public static void copyFolder(File sourceFolder, File destinationFolder) throws MyCustomException {

        try {

            //Check if sourceFolder is a directory or file
            //If sourceFolder is file; then copy the file directly to new location
            if (sourceFolder.isDirectory()) {
                //Verify if destinationFolder is already present; If not then create it
                if (!destinationFolder.exists()) {
                    destinationFolder.mkdir();
                    logger.debug("Directory created :: " + destinationFolder);
                }

                //Get all files from source directory
                String files[] = sourceFolder.list();

                //Iterate over all files and copy them to destinationFolder one by one
                for (String file : files) {
                    File srcFile = new File(sourceFolder, file);
                    File destFile = new File(destinationFolder, file);

                    //Recursive function call
                    copyFolder(srcFile, destFile);
                }
            } else {
                //Copy the file content from one place to another 
                Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.debug("File copied :: " + destinationFolder);
            }

        } catch (IOException ioe) {

            String errorDetails = "IO Error occurred while copying folder: " + ioe.toString();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, NamedConstants.GENERIC_DB_ERR_DESC, errorDetails);
            throw error;
        }
    }

    /**
     *
     * @param absFileName
     * @return
     * @throws com.library.customexception.MyCustomException
     */
    public static boolean createFileOnDisk(String absFileName) throws MyCustomException {

        boolean isCreated = false;

        try {

            File newFile = new File(absFileName);
            //newFile.mkdirs();
            isCreated = newFile.createNewFile();

            return isCreated;

        } catch (FileNotFoundException ex) {

            String errorDescription = "Error! Failed to create file";
            String errorDetails = "FileNotFoundException Error occurred while creating file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;

        } catch (IOException ex) {

            String errorDescription = "Error! Failed to create file";
            String errorDetails = "IO Error occurred while creating file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } catch (SecurityException ex) {

            String errorDescription = "Error! Failed to create file";
            String errorDetails = "SecurityException Error occurred while creating file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.SECURITY_ERR, errorDescription, errorDetails);
            throw error;

        } catch (NullPointerException ex) {

            String errorDescription = "Error! Failed to create file";
            String errorDetails = "NullPointerException Error creating while copying file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.SERVER_ERR, errorDescription, errorDetails);
            throw error;

        } catch (Exception ex) {

            String errorDescription = "Error! Failed to create file";
            String errorDetails = "General Error occurred while creating file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.GENERAL_ERR, errorDescription, errorDetails);
            throw error;

        }

    }

    public static boolean createDirectory(String dir_path) {
        logger.debug("CREATING new DIR: " + dir_path);
        return (new File(dir_path).mkdirs());
    }

    public static void createFileDirectory(String file_path) {
        new File(file_path).getParentFile().mkdirs();
    }

    public static void deleteDirectory(String dir) {
        File dirFile = new File(dir);
        if (dirFile.exists()) {
            File[] children = dirFile.listFiles();
            if ((children != null) && (children.length > 0)) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].isDirectory()) {
                        deleteDirectory(children[i].getAbsolutePath());
                    } else if (children[i].isFile()) {
                        children[i].delete();
                    }
                }
            }
            dirFile.delete();
        }
    }

    public static long getFileCode(String file_path, long file_id) throws MyCustomException {
        int HEAD_LEN = 32;
        int TAIL_LEN = 32;

        int BLOCK_SIZE = 64;
        int BLOCK_NUM = 30;
        int CHECK_LEN = HEAD_LEN + BLOCK_SIZE * BLOCK_NUM + TAIL_LEN;

        RandomAccessFile fs = null;
        byte[] buf = null;
        try {
            File file = new File(file_path);
            if ((!file.exists()) || (!file.isFile())) {
                throw new IllegalStateException("cannot find file: " + file_path);
            }
            int file_id_low = (int) (file_id & 0xFFFFFFFF);
            long size = file.length();
            fs = new RandomAccessFile(file, "r");

            buf = new byte[CHECK_LEN];

            if (size <= CHECK_LEN) {
                readToBuffer(fs, buf, 0, (int) size);
                for (int i = (int) size; i < buf.length; i++) {
                    buf[i] = 0;
                }
            } else {
                int dwDataLen = 0;
                readToBuffer(fs, buf, dwDataLen, HEAD_LEN);
                dwDataLen += HEAD_LEN;

                long interval = (size - CHECK_LEN) / BLOCK_NUM;
                if (interval > 0L) {
                    int i = 0;
                    for (i = 0; i < BLOCK_NUM; i++) {
                        fs.seek(HEAD_LEN + i * (interval + BLOCK_SIZE));
                        readToBuffer(fs, buf, dwDataLen, BLOCK_SIZE);
                        dwDataLen += BLOCK_SIZE;
                    }
                } else {
                    readToBuffer(fs, buf, dwDataLen, BLOCK_SIZE * BLOCK_NUM);
                    dwDataLen += BLOCK_SIZE * BLOCK_NUM;
                }

                fs.seek(size - TAIL_LEN);
                readToBuffer(fs, buf, dwDataLen, TAIL_LEN);
            }

            CRC32 crc32 = new CRC32();
            crc32.update(buf);

            return crc32.getValue();

        } catch (IOException ioex) {

            String errorDescription = "Error! Failed to get File Code";
            String errorDetails = "IO Error occurred while trying to get the file code: " + ioex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {
            buf = null;
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException localIOException2) {
                    logger.error("Error Getting FileCode: " + localIOException2.getMessage());
                }
            }
        }
    }

    public static long getFileSize(String file_path) {
        File file = new File(file_path);
        if ((!file.exists()) || (!file.isFile())) {
            throw new IllegalArgumentException("cannot find file: " + file_path);
        }
        return file.length();
    }

    public static long getFileCRC(String file_path) throws MyCustomException {
        File file = new File(file_path);
        if ((!file.exists()) || (!file.isFile())) {
            throw new IllegalArgumentException("cannot find file: " + file_path);
        }
        int BUF_SIZE = 65536;

        long fileSize = file.length();

        FileInputStream fs = null;
        byte[] buf = null;
        try {
            fs = new FileInputStream(file);

            buf = new byte[65536];

            long oneceSize = 0L;
            long calculated = 0L;
            CRC32 crc32 = new CRC32();
            while (calculated < fileSize) {
                oneceSize = fileSize - calculated;
                if (oneceSize > 65536L) {
                    oneceSize = 65536L;
                }
                int read = 0;
                while (read < oneceSize) {
                    int rlen = fs.read(buf, read, (int) oneceSize - read);
                    if (rlen <= 0) {
                        throw new RuntimeException("Read tar file error: " + file_path);
                    }
                    read += rlen;
                }

                crc32.update(buf, 0, read);
                calculated += oneceSize;
            }

            return crc32.getValue();
        } catch (IOException ioex) {

            String errorDescription = "Error! Failed to get File CRC (sum)";
            String errorDetails = "IO Error occurred while trying to get the file CRC (sum): " + ioex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {
            buf = null;
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException localIOException2) {
            }
        }
    }

    public static boolean existFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     *
     * @param srcFilePath
     * @param destFilePath
     * @return
     * @throws SecurityException
     */
    public static boolean moveFile(String srcFilePath, String destFilePath) throws SecurityException {

        File sourceFile = new File(srcFilePath);
        File destFile = new File(destFilePath);

        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("cannot find source file: " + srcFilePath);
        }

        if (destFile.exists()) {
            throw new IllegalStateException("exited destination file: " + destFilePath);
        }

        destFile.getParentFile().mkdirs();

        return sourceFile.renameTo(destFile);
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() ? file.delete() : true;
    }

    public static boolean deleteFile(File file) {
        return file.exists() ? file.delete() : true;
    }

    public static boolean createNewFile(String filePath) throws MyCustomException {

        logger.debug("createNewFile called!");

        File file = new File(filePath);
        if (!file.exists()) {

            logger.debug("Gonna create new file");

            //check if parent dir exists
            if (!(file.getParentFile().exists())) {
                logger.debug("Creating DIRs: " + file);
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();  //Dont create the file yet
            } catch (IOException ex) {

                String errorDescription = "Error occurred while creating new file";
                String errorDetails = "IO Error occurred while creating new file: " + filePath + "error: " + ex.getMessage();
                MyCustomException error = GeneralUtils.getSingleError(ErrorCode.DATABASE_ERR, errorDescription, errorDetails);
                throw error;
            }

            return Boolean.TRUE;
        } else {

            return Boolean.FALSE;
        }

    }

    /**
     * Read in a '=' line by line delimeted file
     *
     * @param absolutFileName
     * @return
     * @throws MyCustomException
     */
    public static synchronized Map<String, String> readTextFile(String absolutFileName) throws MyCustomException {

        Map<String, String> startIdMap = new HashMap<>();
        String errorDescription = "Error occurred while reading file";

        FileInputStream fstream;
        try {
            fstream = new FileInputStream(absolutFileName);
        } catch (FileNotFoundException ex) {

            String errorDetails = "FileNotFoundException Error occurred while reading text file new file: " + absolutFileName + "error: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {

            String strLine;

            while ((strLine = br.readLine()) != null) {

                logger.debug(strLine);

                String[] tokens = strLine.trim().split("=");
                startIdMap.put(tokens[0], tokens[1]);

            }
        } catch (IOException ex) {

            String errorDetails = "IO Error occurred while creating new file: " + absolutFileName + "error: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;
        }

        return startIdMap;
    }

    /**
     * Read in a '=' line by line delimeted file
     *
     * @param file
     * @return
     * @throws MyCustomException
     */
    public static synchronized Map<String, String> readTextFile(File file) throws MyCustomException {

        Map<String, String> startIdMap = new HashMap<>();
        String errorDescription = "Error occurred while reading file";

        FileInputStream fstream;
        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {

            String errorDetails = "FileNotFoundException Error occurred while reading text file new file: " + file + "error: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {

            String strLine;

            while ((strLine = br.readLine()) != null) {

                if (!strLine.trim().isEmpty()) {

                    logger.debug(strLine);

                    String[] tokens = strLine.trim().split("=");
                    startIdMap.put(tokens[0], tokens[1]);
                }

            }
        } catch (IOException ex) {

            String errorDetails = "IO Error occurred while creating new file: " + file + "error: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;
        }

        return startIdMap;
    }

    /**
     *
     * @param taskId
     * @param newStartId
     * @param absolutFileName
     * @throws com.library.customexception.MyCustomException
     */
    public static synchronized void updateOrAddTextStartId(String taskId, String newStartId, String absolutFileName) throws MyCustomException {

        String input;
        String errorDescription = "Error! Failed to add or update the text start Id in the file";
        try (BufferedReader file = new BufferedReader(new FileReader(absolutFileName))) {
            String line;
            input = "";
            while ((line = file.readLine()) != null) {
                input += line + '\n';
            }
        } catch (IOException ex) {

            String errorDetails = "IO Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;
        }

        logger.debug(input); // check that it's inputted right

        //input = input.replace(target, replacement);
        if (input.matches(taskId + "=\\d+;$")) {
            logger.debug("Pattern has been matched");
            input = input.replaceAll(taskId + "=\\d+;$", taskId + "=" + newStartId + ";");
        } else {
            logger.debug("Pattern not matched, adding this taskId and newStartId to file");
            input += taskId + "=" + newStartId + ";" + '\n';
        }

        // check if the new input is right
        logger.debug("----------------------------------" + '\n' + input);

        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(absolutFileName);
            fileOut.write(input.getBytes());
        } catch (FileNotFoundException ex) {

            String errorDetails = "FileNotFoundException Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;

        } catch (IOException ex) {

            String errorDetails = "IO Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {
            try {
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException ex) {
                logger.error("IO error, closing fileOut connection: " + ex.getMessage());
            }

        }
    }

    /**
     *
     * @param taskIdStartIdMap
     * @param absolutFileName
     * @throws com.library.customexception.MyCustomException
     */
    public static synchronized void addMultipleTextStartIds(Map<Integer, Long> taskIdStartIdMap, String absolutFileName) throws MyCustomException {

        String line = "";
        String errorDescription = "Error! Failed to add or update the text start Id in the file";
        for (Map.Entry<Integer, Long> entry : taskIdStartIdMap.entrySet()) {

            int taskId = entry.getKey();
            long newStartId = entry.getValue();

            line += taskId + "=" + newStartId + ";" + '\n';
        }

        // check if the new input is right
        logger.debug("----------------------------------" + '\n' + line);

        // write the new String with the new contents OVER the same file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(absolutFileName);
            fileOut.write(line.getBytes());
        } catch (FileNotFoundException ex) {

            String errorDetails = "FileNotFoundException Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;

        } catch (IOException ex) {

            String errorDetails = "IO Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {
            try {
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static synchronized void updateMultipleTextStartIds(Map<Integer, Long> taskIdStartIdMap, String absolutFileName) throws MyCustomException {

        String input = "";
        String errorDescription = "Error! Failed to add or update the text start Id in the file";
        try (BufferedReader file = new BufferedReader(new FileReader(absolutFileName))) {
            String line;
            input = "";
            while ((line = file.readLine()) != null) {
                input += line + '\n';
            }
        } catch (IOException ex) {
            String errorDetails = "IO Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;
        }

        for (Map.Entry<Integer, Long> entry : taskIdStartIdMap.entrySet()) {

            int taskId = entry.getKey();
            long newStartId = entry.getValue();

            //input = input.replace(target, replacement);
            input = input.replaceAll(taskId + "=\\d+;$", taskId + "=" + newStartId + ";");
        }

        // check if the new input is right
        logger.debug("----------------------------------" + '\n' + input);

        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(absolutFileName);
            fileOut.write(input.getBytes());
        } catch (FileNotFoundException ex) {

            String errorDetails = "FileNotFoundException Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.FILE_NOT_FOUND_ERR, errorDescription, errorDetails);
            throw error;

        } catch (IOException ex) {

            String errorDetails = "IO Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
            throw error;

        } finally {
            try {
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     *
     * @param fs
     * @param buffer
     * @param offset
     * @param read_size
     * @return
     * @throws MyCustomException
     */
    protected static int readToBuffer(RandomAccessFile fs, byte[] buffer, int offset, int read_size) throws MyCustomException {

        String errorDescription = "Error! Failed to read buffer";

        if ((fs == null) || (buffer == null) || (read_size == 0) || (offset + read_size > buffer.length)) {
            String errorDetails = "Invalid argument";
            MyCustomException error = GeneralUtils.getSingleError(ErrorCode.SERVER_ERR, errorDescription, errorDetails);
            throw error;
        }

        int dwLen = 0;
        int dwRead = 0;
        while (dwRead < read_size) {

            try {

                dwLen = fs.read(buffer, offset + dwRead, read_size - dwRead);

            } catch (IOException ex) {

                String errorDetails = "IO Error occurred while trying to update the text start Id in the file: " + ex.getMessage();
                MyCustomException error = GeneralUtils.getSingleError(ErrorCode.COMMUNICATION_ERR, errorDescription, errorDetails);
                throw error;
            }
            if (dwLen <= 0) {

                String errorDetails = "File exception, File length is invalid";
                MyCustomException error = GeneralUtils.getSingleError(ErrorCode.SERVER_ERR, errorDescription, errorDetails);
                throw error;

            }
            dwRead += dwLen;
        }

        return dwRead;
    }

    /**
     * Get unique upload file name generated from timenow in millis
     *
     * @param fileExt
     * @return
     */
    public static String getFileUploadUniqueName(String fileExt) {

        return (String.valueOf(DateUtils.getTimeNowInLong()) + "." + fileExt);

    }

    /**
     * Convert png image to jpeg
     *
     * @param inputImage
     * @return
     */
    public static BufferedImage convertPNGtoJPG(BufferedImage inputImage) {

        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        outputImage.createGraphics().drawImage(outputImage, 0, 0, Color.WHITE, null);

        return outputImage;

    }

}
