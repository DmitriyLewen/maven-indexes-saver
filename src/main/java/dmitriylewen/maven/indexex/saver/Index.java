package dmitriylewen.maven.indexex.saver;

public class Index { // Used to save indexes to json

    public String sha1;

    public Values values;

    public static class Values{
        public String groupID;
        public String artifactID;
        public String version;

        public Values(String groupID, String artifactID, String version) {
            this.groupID = groupID;
            this.artifactID = artifactID;
            this.version = version;
        }
    }


    public Index(String sha1, String artifactID, String groupID, String version) {
       this.sha1 = sha1;
       this.values = new Values(artifactID, groupID, version);
    }
}
