package pojo;

/**
 * Created by jingbao on 18-11-6.
 */
public class DockerFile {
    private String fileUrl;
    private String projectName;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public DockerFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public DockerFile(){}
}
