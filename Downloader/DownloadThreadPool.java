package Downloader;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DownloadThreadPool {
    private URL url;
    private File file;
    private long length;

    DownloadThreadPool(URL url, File file, long length) {
        this.url = url;
        this.file = file;
        this.length = length;
    }

    void start(int threadNums) {
        ExecutorService threadPool = Executors.newCachedThreadPool();

        for(int i=0; i<threadNums; i++) {
            long start = i * length / threadNums;
            long end = (i + 1) *length / threadNums - 1;
            if(i == threadNums-1) {
                end = length;
            }
            DownloadThread thread = new DownloadThread(file, url, start, end);
            threadPool.submit(thread);
        }
        threadPool.shutdown();
    }


}
