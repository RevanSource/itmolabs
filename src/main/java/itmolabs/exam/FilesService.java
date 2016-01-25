package itmolabs.exam;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FilesService {
    private static final Logger LOGGER = new Logger();

    private static FilesService ourInstance = new FilesService();

    public static FilesService getInstance() {
        return ourInstance;
    }

    private FilesService() {
    }

    public List<String> ls(Path path){
        List<String> result = new ArrayList<>();

        for (File file : path.toFile().listFiles()) {
//            if (file.isDirectory()) {
                result.add(file.getName());
//            }
        }
        return result;
    }

    public Path cd(Path source, String targetName){
        Path target = null;
        try{
            File initial = new File(targetName);
            if (initial.isAbsolute()){
                if (initial.exists())
                    target = Paths.get(targetName);
            } else {
                Path temp = Paths.get(source.toFile().getAbsolutePath(), targetName);
                temp = temp.normalize();
                if (temp.toFile().exists()) {
                    target = temp;
                }
            }
        } catch (Exception e){
            LOGGER.error(e);
        }
        return target;
    }

    public boolean mv(Path source , Path target) {
        Path result = null;
        try {
            result = Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error(e);
            return false;
        }
        return result.equals(target);
    }
}
