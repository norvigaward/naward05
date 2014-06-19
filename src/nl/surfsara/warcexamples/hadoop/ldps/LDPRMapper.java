/**
 * Copyright 2014 SURFsara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfsara.warcexamples.hadoop.ldps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jwat.common.Payload;
import org.jwat.warc.WarcRecord;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.google.common.net.InternetDomainName;

class LDPRMapper extends Mapper<LongWritable, WarcRecord, Text, LongWritable> {
	private static final Log LOG = LogFactory.getLog(LDPRMapper.class);
	private static final Logger logger = Logger.getLogger(LDPRMapper.class);
	private String prevWarcTargetUriStr;
	private String warcTargetUriStrHost;
	public static final int MAXLEN = 10000;
	public static String DEFAULTLANG = "en";
	private int temp = 0;
	
	private static enum Counters {
		CURRENT_RECORD, NUM_TEXT_RECORDS, NUM_NO_IP
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		prevWarcTargetUriStr = "";
		warcTargetUriStrHost = "";
		
		
		File tempdir;
		try {
			// This is ugly, but the langdetect library insists on a folder with
			// its profiles, so we load a ZIP file containing them from the
			// classpath, extract to a temporary directory, and have them loaded
			// from there. Yes, to make a temporary directory we first create a
			// temporary file, then delete it, and then abuse its name for the
			// new directory. Ugly again, but hopefully effective.
			tempdir = File.createTempFile("langdetect-profiles-",
					Long.toString(System.nanoTime()));
			tempdir.delete();
			tempdir.mkdir();
			logger.log(Priority.INFO, "tmpdir: "+tempdir.toString());
			logger.info("LOGGER: tmpdir: "+tempdir.toString());
			LOG.info("LOG: tmpdir: "+tempdir.toString());

			InputStream zipfile = LDPRMapper.class
					.getResourceAsStream("profiles.zip");
			unZip(zipfile, tempdir);

			DetectorFactory.loadProfile(tempdir);
			logger.log(Priority.INFO, "DetectorFactory loaded langs: "+DetectorFactory.getLangList());
			deleteFolder(tempdir);

		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error(e1);
		}
		
	}

	@Override
	public void map(LongWritable key, WarcRecord value, Context context) throws IOException, InterruptedException {
		context.setStatus(Counters.CURRENT_RECORD + ": " + key.get());
		
		String targetURI = value.header.warcTargetUriStr;
				
				
		
		if (null == targetURI || "".equals(targetURI)){
			context.getCounter(Counters.NUM_NO_IP).increment(1);
		} else {
			try {
				warcTargetUriStrHost = new URI(targetURI).getHost();
			} catch (URISyntaxException e) {
				logger.error(e);
			}
		}
		
		// Only process text/plain content
		if ("text/plain".equals(value.header.contentTypeStr) && !(prevWarcTargetUriStr.equals(warcTargetUriStrHost))) {
			context.getCounter(Counters.NUM_TEXT_RECORDS).increment(1);
			// Get the text payload
			Payload payload = value.getPayload();
			
			prevWarcTargetUriStr = warcTargetUriStrHost;
			
			if (payload == null || temp > 50) {
				// NOP
			} else {


				String warcContent = IOUtils.toString(payload.getInputStreamComplete());
				if (warcContent == null && "".equals(warcContent)) {
					// NOP
				} else {
					// Classify text
//					warcContent = warcContent.substring(0, Math.min(warcContent.length(), MAXLEN));
					
					int i = warcContent.indexOf(' ');
					if(i>0){
						String word = warcContent.substring(0, i);
//						context.write(new Text(word), new LongWritable(3));	
						logger.info("LOGGER: tmpdir: "+word);
						LOG.info("LOG: tmpdir: "+word);
					}
					
					
					try {
						Detector detector = DetectorFactory.create();
						detector.append(warcContent);
						context.write(new Text(detector.detect()), new LongWritable(1));
					} catch (LangDetectException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error(e);
					}
				}
				temp++;
			}
		}
	}
	
	public static void unZip(InputStream zippedIS, File outputFolder)
			throws IOException {
		byte[] buffer = new byte[1024];

		ZipInputStream zis = new ZipInputStream(zippedIS);
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {
			String fileName = ze.getName();
			File newFile = new File(outputFolder + File.separator + fileName);
			new File(newFile.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
}
