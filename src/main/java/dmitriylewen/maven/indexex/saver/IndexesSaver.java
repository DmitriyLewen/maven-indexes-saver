package dmitriylewen.maven.indexex.saver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.index.reader.*;
import org.apache.maven.index.reader.Record;
import org.apache.maven.index.reader.resource.UrlResource;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.maven.index.reader.Record.*;
import static org.apache.maven.index.reader.Record.Type.ARTIFACT_ADD;
import static org.apache.maven.index.reader.Utils.loadProperties;

public class IndexesSaver {
    private static final String MAVEN_REPOSITORY_BASE_URL = "https://repo.maven.apache.org/maven2/.index/";
    private static final String ARCHIVE_NAME = "nexus-maven-repository-index.%d.gz";
    private static final String PROPERTIES_FILE_NAME = "nexus-maven-repository-index.properties";
    private static final String LAST_INCREMENTAL_INDEX = "nexus.index.last-incremental";
    private static final String JSON_FILES_DIR = "/home/dmitriy/work/temp/3427/1111/jsons/";
    private static final String JSON_FILE_NAME_FORMAT = "%d.json";


    protected static  List<Record> loadArtifactsAddRecords(final ChunkReader chunkReader)
            throws IOException {
        List<Record> records = new ArrayList<>();
        try (chunkReader) {
            final RecordExpander recordExpander = new RecordExpander();
            for (Map<String, String> rec : chunkReader) {
                final Record record = recordExpander.apply(rec);
                if (record.getType() == ARTIFACT_ADD){
                    records.add(record);
                }
            }
        }
        return records;
    }

    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger(IndexesSaver.class);
        if (args.length != 1 || !Objects.equals(args[0], "DEBUG")) {
            logger.setLevel(Level.INFO);
        }

        int lastIndex;
        // get the latest index to start parsing from this archive
        try (UrlResource urlResource = new UrlResource(new URL(MAVEN_REPOSITORY_BASE_URL + PROPERTIES_FILE_NAME));
             ResourceHandler remote = new SaverResourceHandler(urlResource)) {
            Properties props = loadProperties(remote.locate(""));
            lastIndex = Integer.parseInt(props.getProperty(LAST_INCREMENTAL_INDEX));
            logger.info("last index has been found: " + lastIndex);
        }

        int numberOfIndexes = 0; // number of indexes found from all archives
        for (int i = lastIndex; ; i--) { // parse all archives from lastIndex to last existing one
            try (UrlResource urlResource = new UrlResource(new URL(String.format(MAVEN_REPOSITORY_BASE_URL + ARCHIVE_NAME, i)));
                 WritableResourceHandler handler = new SaverWritableResourceHandler(urlResource)) {

                ChunkReader chunkReader = new ChunkReader("full",
                        handler.locate("").read());

                ArrayList<Index> indexes = new ArrayList<>();
                final List<Record> artifacts = loadArtifactsAddRecords(chunkReader);
                for (var artifact : artifacts) {
                    String sha1 = artifact.getString(SHA1);
                    String artifactID = artifact.getString(ARTIFACT_ID);
                    String groupID = artifact.getString(GROUP_ID);
                    String version = artifact.getString(VERSION);
                    Index index = new Index(sha1, artifactID, groupID, version);
                    indexes.add(index);
                    numberOfIndexes++;
                }
                // save indexes to json file
                Files.createDirectories(Paths.get(JSON_FILES_DIR));
                String path = String.format(JSON_FILES_DIR + JSON_FILE_NAME_FORMAT, i);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(new File(path), indexes);
                    logger.debug("indexes from " + String.format(ARCHIVE_NAME, i) + " has been saved to json file " + path);
                } catch (IOException e) {
                    logger.error("can't save indexes to json: " + e);
                }

            } catch (FileNotFoundException e) {
                // maven repository doesn't have some first archives
                // e.g. from 787 to 161
                // after got exception - stop parse
                logger.info("Parse of archives finished.");
                logger.info("Last archive: " + String.format(ARCHIVE_NAME, i - 1));
                logger.info(String.format("Saved %d indexes", numberOfIndexes));
                break;
            } catch (IOException e) {
                logger.error("unexpected error: " + e);
            }

        }
    }
}