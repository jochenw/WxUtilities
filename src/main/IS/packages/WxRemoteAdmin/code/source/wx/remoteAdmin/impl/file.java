package wx.remoteAdmin.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.ServerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class file

{
	// ---( internal utility methods )---

	final static file _instance = new file();

	static file _newInstance() { return new file(); }

	static file _cast(Object o) { return (file)o; }

	// ---( server methods )---




	public static final void createZipFileFromDirectory (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(createZipFileFromDirectory)>> ---
		// @sigtype java 3.5
		// [i] field:0:required directory
		// [o] object:0:required bytes
		final IDataMap pipe = new IDataMap(pipeline);
		final String directory = requireParameter(pipe, "directory");
		final Path dir = Paths.get(directory);
		if (!Files.isDirectory(dir)) {
			throw new IllegalArgumentException("Invalid value for parameter 'directory':"
					+ " Expected existing directory, got " + directory);
		}
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			final byte[] buffer = new byte[8192];
			final FileVisitor<Path> fv = new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path pPath, BasicFileAttributes pAttrs) throws IOException {
					final Path p = dir.relativize(pPath);
					final ZipEntry ze = new ZipEntry(p.toString());
					ze.setMethod(ZipEntry.DEFLATED);
					ze.setCompressedSize(pAttrs.size());
					ze.setLastModifiedTime(pAttrs.lastModifiedTime());
					zos.putNextEntry(ze);
					try (InputStream in = Files.newInputStream(pPath)) {
						for(;;) {
							final int res = in.read(buffer);
							if (res == -1) {
								break;
							} else if (res > 0) {
								zos.write(buffer, 0, res);
							}
						}
					}
					zos.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			};
			Files.walkFileTree(dir, fv);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		pipe.put("bytes", baos.toByteArray());
		// --- <<IS-END>> ---

                
	}



	public static final void getFileFromDirectory (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFileFromDirectory)>> ---
		// @sigtype java 3.5
		// [i] field:0:required directory
		// [i] field:0:required path
		// [o] object:0:required bytes
		final IDataMap pipe = new IDataMap(pipeline);
		final String directory = requireParameter(pipe, "directory");
		final Path dir = Paths.get(directory);
		if (!Files.isDirectory(dir)) {
			throw new IllegalArgumentException("Invalid value for parameter directory:"
					+ " Expected existing directory, got " + directory);
		}
		final String pathStr = requireParameter(pipe, "path");
		final Path path = dir.resolve(pathStr);
		// Security: Do not permit access to files outside of dir.
		if (!isInDirectory(dir,path)) {
			throw new IllegalArgumentException("Invalid value for parameter path:"
					+ " Expected a path, which is inside " + dir + ", got " + pathStr);
		}
		if (!Files.isRegularFile(path)) {
			throw new IllegalArgumentException("Invalid value for parameter path:"
					+ " Expected existing file, got " + path.toString());
		}
		final byte[] bytes;
		try {
			bytes = Files.readAllBytes(path);
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		pipe.put("bytes", bytes);
		// --- <<IS-END>> ---

                
	}



	public static final void listFilesForDirectory (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(listFilesForDirectory)>> ---
		// @sigtype java 3.5
		// [i] field:0:required directory
		// [i] field:0:optional sortBy {"pathAndDateTime","path","dateTime"}
		// [i] field:0:optional pathsRelativeTo {"givenDirectory","currentDirectory"}
		// [o] recref:1:required files wx.remoteAdmin.pub:remoteFile
		final IDataMap pipe = new IDataMap(pipeline);
		final String directory = requireParameter(pipe, "directory");
		final Path dir = Paths.get(directory);
		final String pathsRelativeTo = pipe.getAsString("pathsRelativeTo", "givenDirectory");
		if (!pathsRelativeTo.equals("givenDirectory")  &&  !pathsRelativeTo.equals("currentDirectory")) {
			throw new IllegalArgumentException("Invalid value for parameter pathsRelativeTo:"
					+ " Expected givenDirectory|currentDirectory, got " + pathsRelativeTo);
		}
		final String sortBy = pipe.getAsString("sortBy", "pathAndDateTime");
		final Comparator<RemoteFile> sorter;
		switch (sortBy) {
		case "path":
			sorter = (rf1,rf2) -> rf1.getPath().compareToIgnoreCase(rf2.getPath());
			break;
		case "dateTime":
			sorter = (rf1,rf2) -> rf1.getDateTime().compareTo(rf2.getDateTime());
			break;
		case "pathAndDateTime":
			sorter = (rf1,rf2) -> {
				final int result = rf1.getPath().compareToIgnoreCase(rf2.getPath());
				if (result == 0) {
					return rf1.getDateTime().compareTo(rf2.getDateTime());
				} else {
					return result;
				}
			};
			break;
		default:
			throw new IllegalArgumentException("Invalid value for parameter sortBy:"
					+ " Expected pathAndDateTime|path|dateTime, got " + sortBy);
		}
		final List<RemoteFile> files = new ArrayList<>();
		if (Files.isDirectory(dir)) {
			final FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path pPath, BasicFileAttributes pAttrs) throws IOException {
					final Path path;
					if ("givenDirectory".equals(pathsRelativeTo)) {
						path = dir.relativize(pPath);
					} else {
						path = pPath;
					}
					final FileTime fileTime = pAttrs.lastModifiedTime();
					final LocalDateTime localDateTime = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
					files.add(new RemoteFile(path.toString(), localDateTime));
					return FileVisitResult.CONTINUE;
				}
				
			};
			try {
				Files.walkFileTree(dir, fv);
			} catch (IOException e) {
				throw new ServiceException(e);
			}
		}
		files.sort(sorter);
		final IData[] array = new IData[files.size()];
		for (int i = 0;  i < array.length;  i++) {
			final RemoteFile rf = files.get(i);
			final IData data = IDataFactory.create();
			final IDataCursor crsr = data.getCursor();
			IDataUtil.put(crsr, "path", rf.getPath());
			final LocalDateTime ldt = rf.getDateTime();
			IDataUtil.put(crsr, "dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt));
			crsr.destroy();
			array[i] = data;
		}
		pipe.put("files", array);		
			
		// --- <<IS-END>> ---

                
	}



	public static final void readFileToBytes (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(readFileToBytes)>> ---
		// @sigtype java 3.5
		// [i] field:0:required path
		// [o] object:0:required bytes
		final IDataMap pipe = new IDataMap(pipeline);
		final String pathStr = requireParameter(pipe, "path");
		final Path path = Paths.get(pathStr);
		if (Files.isRegularFile(path)) {
			final byte[] bytes;
			try {
				bytes = Files.readAllBytes(path);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			pipe.put("bytes", bytes);
		} else {
			throw new IllegalArgumentException("Invalid value for parameter path:"
					+ " Expected existing file, got " + pathStr);
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void writeFileToDirectory (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(writeFileToDirectory)>> ---
		// @sigtype java 3.5
		// [i] field:0:required directory
		// [i] field:0:required fileName
		// [i] record:0:required content
		// [i] - object:0:required stream
		// [i] - object:0:required bytes
		// [i] - field:0:required fileName
		// [i] field:0:optional keepBackup {"false","true"}
		// [o] field:0:required createdFile
		final IDataMap pipe = new IDataMap(pipeline);
		final String directory = requireParameter(pipe, "directory");
		final String fileName = requireParameter(pipe, "fileName");
		final Path dir = Paths.get(directory);
		final Path file = dir.resolve(fileName);
		if (!isInDirectory(dir, file)) {
			throw new IllegalArgumentException("Invalid value for parameter fileName:"
					+ " File " + file + " is not within directory " + dir);
		}
		final boolean keepBackup = pipe.getAsBoolean("keepBackup", Boolean.FALSE).booleanValue();
		final IData contentDocument = pipe.getAsIData("content");
		if (contentDocument == null) {
			throw new NullPointerException("Missing parameter: content");
		}
		final IDataMap contentMap = new IDataMap(contentDocument);
		int numberOfSourceStreams = 0;
		Supplier<InputStream> streamSupplier = null;
		final Object stream = contentMap.get("stream");
		if (stream != null) {
			if (stream instanceof InputStream) {
				streamSupplier = () -> (InputStream) stream;
				++numberOfSourceStreams;
			} else {
				throw new IllegalArgumentException("Invalid value for parameter content/stream:"
						+ " Expected InputStream, got " + stream.getClass().getName() + ":" + stream);
			}
		}
		final Object bytes = contentMap.get("bytes");
		if (bytes != null) {
			if (bytes instanceof byte[]) {
				streamSupplier = () -> new ByteArrayInputStream((byte[]) bytes);
				++numberOfSourceStreams;
			} else {
				throw new IllegalArgumentException("Invalid value for parameter content/bytes:"
						+ " Expected byte array, got " + bytes.getClass().getName() + ":" + bytes);
			}
		}
		final String streamFileName = contentMap.getAsString("fileName");
		if (streamFileName != null) {
			final Path filePath = Paths.get(streamFileName);
			if (Files.isRegularFile(filePath)) {
				streamSupplier = () -> {
					try {
						return Files.newInputStream(filePath);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				};
				++numberOfSourceStreams;
			} else {
				throw new IllegalArgumentException("Invalid value for parameter content/fileName:"
						+ " Expected existing file, got " + fileName);
			}
		}
		if (streamSupplier == null) {
			throw new NullPointerException("Missing parameter: content/stream, content/bytes, or content/fileName");
		}
		if (numberOfSourceStreams > 1) {
			throw new IllegalArgumentException("Invalid parameters:"
					+ "content/stream, content/bytes, and content/fileName are mutually exclusive");
		}
		final BiConsumer<Integer,Path> backupCreator;
		if (keepBackup) {
			backupCreator = new BiConsumer<Integer,Path>() {
				public void accept(Integer pIndex, Path pPath) {
					final Path d = file.getParent();
					final String fn = d.getFileName().toString();
					final int offset = fn.indexOf('.');
					final String targetFileName;
					final String ext = pIndex == null ? "-bak" : ("-bak(" + pIndex + ")");
					if (offset == -1  ||  offset == 0  ||  offset+1 == fn.length()) {
						targetFileName = fn + ext;
					} else {
						targetFileName = fn.substring(0, offset) + ext + fn.substring(offset);
					}
					final Path targetFile = d.resolve(targetFileName);
					if (Files.exists(targetFile)) {
						final int nextIndex = pIndex == null ? 2 : pIndex.intValue()+1;
						accept(Integer.valueOf(nextIndex), targetFile);
						try {
							Files.move(pPath, targetFile);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					}
				}
			};
		} else {
			backupCreator = (i,p) -> { /* Do nothing */ };
		}
		backupCreator.accept(null, file);
		final Path fileDir = file.getParent();
		if (fileDir != null) {
			try {
				Files.createDirectories(fileDir);
			} catch (IOException e) {
				
			}
		}
		try (InputStream in = streamSupplier.get();
			 OutputStream out = Files.newOutputStream(file)) {
			final byte[] buffer = new byte[8192];
			for (;;) {
				final int res = in.read(buffer);
				if (res == -1) {
					break;
				} else if (res > 0) {
					out.write(buffer, 0, res);
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		pipe.put("createdFile", file.toString());
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static class RemoteFile {
		private final String path;
		private final LocalDateTime dateTime;
		public RemoteFile(String pPath, LocalDateTime pDateTime) {
			path = pPath;
			dateTime = pDateTime;
		}
		public String getPath() { return path; }
		public LocalDateTime getDateTime() { return dateTime; }
	}
	
	private static String requireParameter(IDataMap pMap, String pParameter) {
		final String value = pMap.getAsString(pParameter);
		if (value == null) {
			throw new NullPointerException("Missing parameter: " + pParameter);
		}
		if (value.length() == 0) {
			throw new IllegalArgumentException("Empty parameter: " + pParameter);
		}
		return value;
	}
	private static boolean isInDirectory(Path pDir, Path pFile) {
		final Path dir = pDir.toAbsolutePath();
		Path file = pFile.toAbsolutePath();
		while (file != null) {
			if (dir.equals(file)) {
				return true;
			} else {
				file = file.getParent();
			}
		}
		return false;
	}
		
	// --- <<IS-END-SHARED>> ---
}

