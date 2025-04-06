package johnsson84.ycFiles.service;

import jdk.jfr.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    ///////////// FILES /////////////
    // Upload a file
    public String uploadFile(String user, String folder, MultipartFile file) {
        // Save path
        String userMail = !user.isEmpty() ? user : "noname";
        String folderName = !folder.isEmpty() ? folder : "Upload";
        String folderPath = String.format("%s/files/%s/%s/", System.getProperty("user.dir"), userMail, folderName);
        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileUploadStatus = "";

        try {
            // Create object
            FileOutputStream fileOut = new FileOutputStream(folderPath + file.getOriginalFilename());
            fileOut.write(file.getBytes());
            // Close connection
            fileOut.close();
            fileUploadStatus = "File uploaded successfully";
        } catch (Exception e) {
            e.printStackTrace();
            fileUploadStatus = "Error in uploading file: " + e;
        }
        return fileUploadStatus;
    }

    // List files from a folder
    public String[] getFiles(String user, String folder) {
        String folderPath = String.format("%s/files/%s/%s/", System.getProperty("user.dir"), user, folder);

        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String[] files = directory.list();
        return files;
    }

    // Delete a file from a folder
    public String deleteFile(String user, String folder, String fileName) {
        String filePath = String.format("%s/files/%s/%s/%s", System.getProperty("user.dir"), user, folder, fileName);
        File file = new File(filePath);
        if (!file.exists()) {
            return "File does not exist";
        }
        file.delete();
        return "File deleted successfully";
    }

    // Download a file
    public ResponseEntity<?> downloadFile(String user, String folder, String fileName) throws IOException {
        String filePath = String.format("%s/files/%s/%s/%s", System.getProperty("user.dir"), user, folder, fileName);
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File does not exist");
        }

        // Media type
        String contentType = Files.probeContentType(file.toPath());
        MediaType mediaType = (contentType != null) ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

        // Read file
        byte[] data = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(data);
    }

    ///////////// FOLDERS /////////////
    // Add a folder
    public String addFolder(String user, String folderName) {
        // Set folderpath
        String fodlerpath = String.format("%s/files/%s/%s/", System.getProperty("user.dir"), user, folderName);
        String response = "";
        try {
            File directory = new File(fodlerpath);
            directory.mkdirs();
            response = "Folder added successfully";
        } catch (Exception e) {
            response = "Error in adding folder: " + e;
        }
        return response;
    }

    // List folders from a user
    public List<String> getFolders(String user) {
        String folderPath = String.format("%s/files/%s/", System.getProperty("user.dir"), user);
        File userfolder = new File(folderPath);
        List<String> folders = new ArrayList<>();

        if (userfolder.exists() && userfolder.isDirectory()) {
            File[] files = userfolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    folders.add(file.getName());
                }
            } else {
                return folders;
            }
        }

        return folders;
    }

    // Delete a folder with its files
    public String deleteFolder(String user, String folder) throws Exception {
        String folderPath = String.format("%s/files/%s/%s", System.getProperty("user.dir"), user, folder);
        File folderToDelete = new File(folderPath);
        if (folderToDelete.exists() && folderToDelete.isDirectory()) {
            File[] files = folderToDelete.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            folderToDelete.delete();
        } else {
            throw new Exception("Folder does not exist");
        }
        return "Folder deleted successfully";
    }
}
