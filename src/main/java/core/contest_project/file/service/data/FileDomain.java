package core.contest_project.file.service.data;

import core.contest_project.file.FileLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FileDomain {
    private Long id;
    private FileInfo info;
    private String url;


    public void setUrl(String url) {
        this.url = url;
    }

    public void setStoreFileName(String storeFileName) {
        if(info==null){info=FileInfo.builder().build();}

        info.setStoreFileName(storeFileName);
    }


    public void setFileLocation(FileLocation location) {
        if(info==null){info=FileInfo.builder().build();}
        info.setLocation(location);
    }

    public String getStoreFileName() {
        if(info==null){return null;}
        return info.getStoreFileName();
    }


    public String getUrl(){
        if(url == null){
            return null;
        }
        return url;
    }
}
