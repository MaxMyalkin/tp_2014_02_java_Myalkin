package VFS;

/*
 * Created by maxim on 12.04.14.
 */

import java.io.File;
import java.util.*;

class FileNameIterator implements Iterator<String> {

    private Queue<File> filesList = new LinkedList<>();

    public FileNameIterator(String filepath) {
        filesList.add(new File(filepath));
    }

    public boolean hasNext() {
        return !filesList.isEmpty();
    }

    @Override
    public String next() {
        File file = filesList.poll();
        if(file.isDirectory()){
            Collections.addAll(filesList, file.listFiles());
        }
        return file.getAbsolutePath();
    }

    @Override
    public void remove() {
        filesList.poll();
    }

}