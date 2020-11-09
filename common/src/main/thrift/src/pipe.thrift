namespace java com.lcaaaat.pipe.common.thrift

struct File {
  1: required string name
  2: required string relativePath
  3: required string checksum
}

struct Directory {
  1: required string name
  2: required string relativePath
  3: required list<Directory> subDirectories
  4: required list<File> subFiles
}

struct Project {
  1: required string name
  2: optional string description
  3: optional string remotePath
}

exception PipeRemoteException {
  1: required string reason
}

service Pipe {
  bool containsProject(1: Project project) throws (1: PipeRemoteException e)
  void registerProject(1: Project project) throws (1: PipeRemoteException e)
  Directory listDirectory(1: Project project) throws (1: PipeRemoteException e)
  void createDirectory(1: Project project, 2: Directory directory) throws (1: PipeRemoteException e)
  void removeDirectory(1: Project project, 2: Directory directory) throws (1: PipeRemoteException e)
  void uploadFile(1: Project project, 2: File file, 3: binary data) throws (1: PipeRemoteException e)
  void removeFile(1: Project project, 2: File file) throws (1: PipeRemoteException e)
}

