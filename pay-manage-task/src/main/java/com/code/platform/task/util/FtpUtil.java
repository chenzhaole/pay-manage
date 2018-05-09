package com.code.platform.task.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.TimeZone;

public class FtpUtil {
    private FTPClient ftpClient = null;
    private String strIp = "";
    private int intPort = 21;
    private String user = "";
    private String password = "";
    private int returnCode;

    private static Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /* *
     * Ftp构造函数
     */
    public FtpUtil(String strIp, int intPort, String user, String Password) {
        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = Password;
        this.ftpClient = new FTPClient();
    }

    /**
     * @return 判断是否登入成功
     */
    public boolean ftpLogin() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("UTF-8");
        this.ftpClient.configure(ftpClientConfig);
        // 设置被动模式
        this.ftpClient.enterLocalPassiveMode();
        this.ftpClient.setConnectTimeout(2000);
        try {
            if (this.intPort > 0) {
                this.ftpClient.connect(this.strIp, this.intPort);
            } else {
                this.ftpClient.connect(this.strIp);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                this.ftpClient = null;
                logger.error("登录FTP服务失败！");
                return false;
            }
            this.ftpClient.login(this.user, this.password);
            // 设置传输协议
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            logger.info("成功登陆FTP服务器");
            isLogin = true;
        } catch (Exception e) {
            logger.error("登录FTP服务失败！" + e.getMessage());
        }
        this.ftpClient.setBufferSize(1024 * 2);
        this.ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }

    /**
     * 退出关闭服务器链接
     */
    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    logger.info("成功退出服务器");
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.warn("退出FTP服务器异常！" + e.getMessage());
            } finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.warn("关闭FTP服务器的连接异常！");
                }
            }
        }
    }

    /**
     * 上传Ftp文件
     *
     * @param localFile        当地文件
     * @param remotUpLoadePath 上传服务器路径 - 应该以/结束
     */
    public boolean uploadFile(File localFile, String remotUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            if (!localFile.exists()) {
                logger.error("file not exists :" + localFile.toString());
                return false;
            }
            //会自动替换旧文件
//            String strFilePath = romotUpLoadePath + localFile.getName();
//            if(checkRemoteFileExists(strFilePath)){
//                deleteRemoteFile(strFilePath);
//            }
            ftpCreateDirectoryTree(remotUpLoadePath);
            this.ftpClient.changeWorkingDirectory(remotUpLoadePath);// 改变工作路径
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            logger.debug(localFile.getName() + "开始上传.....");
            success = this.ftpClient.storeFile(localFile.getName(), inStream);
            logger.debug(localFile.getName() + "上传成功");
            return success;
        } catch (FileNotFoundException e) {
            logger.error(localFile + "未找到");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return success;
    }

    /**
     * 下载文件
     *
     * @param remoteFileName     待下载文件名称
     * @param localDires         下载到当地那个路径下
     * @param remoteDownLoadPath remoteFileName所在的路径
     */

    public boolean downloadFile(String remoteFileName, String localDires, String remoteDownLoadPath) {
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            File local_file = new File(strFilePath);
            if (local_file.exists()) {
                if (!local_file.delete()) {
                    logger.error("file delete error :" + local_file);
                    return false;
                }
            }
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            logger.debug(remoteFileName + "开始下载....");
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            logger.debug(remoteFileName + "成功下载到" + strFilePath);
            if (!success) {
                logger.error(remoteFileName + "下载失败!!!");
            }
            return success;
        } catch (Exception e) {
            logger.error(remoteFileName + "下载失败");
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return success;
    }

    /**
     * 上传文件夹
     *
     * @param localDirectory      当地文件夹
     * @param remoteDirectoryPath Ftp 服务器路径 以目录"/"结束
     */
    public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
        File src = new File(localDirectory);
        if (!src.exists()) {
            return false;
        }
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            ftpCreateDirectoryTree(remoteDirectoryPath);
        } catch (IOException e) {
            logger.error(remoteDirectoryPath + "目录创建失败");
        }
        File[] allFile = src.listFiles();
        if (allFile != null) {
            for (File anAllFile : allFile) {
                if (!anAllFile.isDirectory()) {
                    String srcName = anAllFile.getPath();
                    uploadFile(new File(srcName), remoteDirectoryPath);
                } else {
                    uploadDirectory(anAllFile.getPath(), remoteDirectoryPath);
                }
            }
        }
        return true;
    }

    /**
     * 下载文件夹
     *
     * @param localDirectoryPath 本地地址
     * @param remoteDirectory    远程文件夹
     */
    public boolean downLoadDirectory(String localDirectoryPath, String remoteDirectory) {
        try {
            String fileName = new File(remoteDirectory).getName();
            localDirectoryPath = localDirectoryPath + fileName + "//";
            File filePath = new File(localDirectoryPath);
            if (!checkFileExists(localDirectoryPath)) {
                if (!filePath.mkdir()) {
                    logger.error("create localPath error: " + localDirectoryPath);
                    return false;
                }
            }
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
            for (FTPFile anAllFile : allFile) {
                if (!anAllFile.isDirectory()) {
                    downloadFile(anAllFile.getName(), localDirectoryPath, remoteDirectory);
                } else {
                    String strremoteDirectoryPath = remoteDirectory + "/" + anAllFile.getName();
                    downLoadDirectory(localDirectoryPath, strremoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            logger.error("下载文件夹失败");
            return false;
        }
        return true;
    }

    public void ftpCreateDirectoryTree(String dirTree) throws IOException {
        boolean dirExists = true;
        //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
        String[] directories = dirTree.split("/");
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (dirExists) {
                    dirExists = ftpClient.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    if (!ftpClient.makeDirectory(dir)) {
                        throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + ftpClient.getReplyString() + "'");
                    }
                    if (!ftpClient.changeWorkingDirectory(dir)) {
                        throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + ftpClient.getReplyString() + "'");
                    }
                }
            }
        }
    }

    /**
     * Determines whether a directory exists or not
     *
     * @param dirPath
     * @return true if exists, false otherwise
     * @throws java.io.IOException thrown if any I/O error occurred.
     */
    boolean checkDirectoryExists(String dirPath) throws IOException {
        ftpClient.changeWorkingDirectory(dirPath);
        returnCode = ftpClient.getReplyCode();
        if (returnCode == 550) {
            return false;
        }
        return true;
    }

    /**
     * Determines whether a file exists or not
     *
     * @param filePath
     * @return true if exists, false otherwise
     * @throws java.io.IOException thrown if any I/O error occurred.
     */
    boolean checkRemoteFileExists(String filePath) throws IOException {
        InputStream inputStream = ftpClient.retrieveFileStream(filePath);
        returnCode = ftpClient.getReplyCode();
        if (inputStream == null || returnCode == 550) {
            return false;
        }
        return true;
    }

    public boolean deleteRemoteFile(String remoteFile) {
        try {
            this.ftpClient.deleteFile(remoteFile);
            return true;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath p文件路径
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean checkFileExists(String filePath) throws IOException {
        boolean flag;
        File file = new File(filePath);
        if (!file.exists()) {
            flag = false;
            logger.error("文件不存在,请检查!");
        } else {
            flag = true;
        }
        return flag;
    }

    // FtpClient的Set 和 Get 函数
    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public static void main(String[] args) throws IOException {
        FtpUtil ftp = new FtpUtil("192.168.3.210", 21, "ftpuser", "ftpuser");
        ftp.ftpLogin();
//        ftp.uploadFile(new File("H:\\shangcheng\\sy-manager-war\\target\\webroot\\userfiles\\product\\img\\20131224\\tvcenter\\1387853714391.jpg"), "userfiles/product/img/20131224/tvcenter/");
        //上传文件夹
//        ftp.uploadDirectory("H:\\shangcheng\\sy-manager-war\\target\\webroot\\userfiles\\product\\img\\20131224\\tvcenter\\", "/userfiles/product/DataProtemp");
        //下载文件夹
//        ftp.downLoadDirectory("H:\\shangcheng\\sy-manager-war\\target\\webroot\\userfiles\\product\\img\\20131224\\tvcenter\\", "/userfiles/product/DataProtemp");
//        ftp.deleteRemoteFile("test/test.txt");
        ftp.ftpLogOut();
    }
}
