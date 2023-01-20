package dmitriylewen.maven.indexex.saver;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class GitWorker {
    private Git git;
    private final String token;

    public GitWorker(String token) {
        this.token = token;
    }

    public void clone(String repoURL, File localDirPath) throws GitAPIException {
        git = Git.cloneRepository()
                .setURI(repoURL)
                .setDirectory(localDirPath)
                .call();
    }

    public void open(File localDirPath) throws IOException {
        git = Git.open(localDirPath);
    }



    public boolean addUncommitted() throws GitAPIException {
        Status status = git.status().call();
        boolean hasUncommittedChanges = status.hasUncommittedChanges();
        if (hasUncommittedChanges){
            for (String uncommitted: status.getUncommittedChanges()){
                git.add().addFilepattern(uncommitted).call();
            }
        }
        return hasUncommittedChanges;
    }

    public void commit(String commitMessage) throws GitAPIException {
        git.commit().setMessage(commitMessage).call();
    }
    public void push() throws GitAPIException {
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
    }
}
