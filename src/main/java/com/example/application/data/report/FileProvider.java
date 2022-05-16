package com.example.application.data.report;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class FileProvider {

    private final String ACCESS_TOKEN = "iHKDP873CaIAAAAAAAAAARitTd5LeskZ0lU7YjdHFH_ai0Trz2XjvcLow1JY6qLP";
    private final DbxClientV2 client;

    public FileProvider() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
        deleteOldFiles();
    }

    private void deleteOldFiles() {
        List<Metadata> files;
        try {
            files = client.files().listFolder("/Сформированные отчёты").getEntries();
            for (Metadata fileMetadata : files) {
                if (!(fileMetadata instanceof FolderMetadata)) {
                    String fileName = fileMetadata.getName();
                    String fullPath = "/Сформированные отчёты/" + fileName;
                    FileMetadata fileMetaData = (FileMetadata) client.files().getMetadata(fullPath);
                    Date fileDate = fileMetaData.getServerModified();
                    if (fileDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now().minusMonths(3)))
                        deleteFileFromDropbox(fullPath);
                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public boolean checkFileExistence(String filePath) {
        boolean exists = false;
        try {
            InputStream is = client.files().download(filePath).getInputStream();
            if (is != null) {
                exists = true;
                is.close();
            }
        } catch (IOException | DbxException ex) {
            exists = false;
        }
        return exists;
    }

    public XWPFDocument readDocxFileFromDropbox(String fileName) {
        try {
            InputStream is = client.files().download("/" + fileName).getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes));
            is.close();

            return document;
        } catch (IOException | DbxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public XSSFWorkbook readExcelFileFromDropbox(String fileName) {
        try {
            InputStream is = client.files().download("/" + fileName).getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            XSSFWorkbook document = new XSSFWorkbook(new ByteArrayInputStream(bytes));
            is.close();

            return document;
        } catch (IOException | DbxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void uploadFile(XWPFDocument localFile, String dropboxPath) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        localFile.write(out);
        out.close();
        localFile.close();
        byte[] xwpfDocumentBytes = out.toByteArray();

        try (InputStream in = new ByteArrayInputStream(xwpfDocumentBytes)) {
            client.files().uploadBuilder(dropboxPath)
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(in);
        } catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading from file \"" + localFile + "\": " + ex.getMessage());
            System.exit(1);
        }
    }

    public String createSharedLink(String dropboxPath) {
        try {
            ListSharedLinksResult result = client.sharing().listSharedLinksBuilder()
                    .withPath(dropboxPath).withDirectOnly(true).start();
            SharedLinkMetadata sharedLinkMetadata;
            if (result.getLinks().isEmpty())
                sharedLinkMetadata = client.sharing().createSharedLinkWithSettings(dropboxPath);
            else
                sharedLinkMetadata = result.getLinks().get(0);
            return sharedLinkMetadata.getUrl();
        } catch (DbxException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void uploadExcelToDropbox(MemoryBuffer buffer, String dropboxPath) {
        try (InputStream is = buffer.getInputStream()) {
            client.files().uploadBuilder(dropboxPath)
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(is);
        } catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading from file \"" + dropboxPath + "\": " + ex.getMessage());
            System.exit(1);
        }
    }

    public void deleteFileFromDropbox(String fileName) {
        try {
            client.files().deleteV2(fileName);
        } catch (DbxException ex) {
            ex.printStackTrace();
        }
    }
}
