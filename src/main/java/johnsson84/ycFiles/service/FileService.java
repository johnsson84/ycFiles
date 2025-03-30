package johnsson84.ycFiles.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

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

    public String[] getFiles(String user, String folder) {
        String folderPath = String.format("%s/files/%s/%s/", System.getProperty("user.dir"), user, folder);

        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String[] files = directory.list();
        return files;
    }

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
}
