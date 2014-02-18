package org.kew.stringmod.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface Dict {

    public void readFile() throws IOException;
    public String getFileDelimiter();
    public void setFileDelimiter(String fileDelimiter);
    public String getFilePath();
    public void setFilePath(String filePath) throws IOException;
    public Set<Map.Entry<String,String>> entrySet();
    public String get(String key);

}
