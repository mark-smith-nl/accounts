package nl.smith.account.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nl.smith.account.domain.MutationFile;
import nl.smith.account.persistence.MutationFileMapper;

@Service
public class MutationFileService {

	private final MutationFileMapper mutationFileMapper;

	public MutationFileService(MutationFileMapper mutationFileMapper) {
		this.mutationFileMapper = mutationFileMapper;
	}

	public void persist(String directoryName, String fileName) {
		Path path = Paths.get(directoryName, fileName);

		if (path.isAbsolute() && Files.exists(path)) {
			String absoluteFilePath = path.normalize().toString();
			try {
				byte[] fileBytes = Files.readAllBytes(path);
				MutationFile mutationFile = new MutationFile(absoluteFilePath, fileBytes, 0);
				mutationFileMapper.persist(mutationFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Not found: " + path.normalize().toString());
		}

		// MutationFile file = new MutationFile(fileName, fileData, checksum)
	}

	public Optional<MutationFile> getMutationFileByAbsoluteFilePath(String directoryName, String fileName) {
		Path path = Paths.get(directoryName, fileName);

		String absoluteFilePath = path.normalize().toString();

		return Optional.ofNullable(mutationFileMapper.getMutationFileByAbsoluteFilePath(absoluteFilePath));
	}

	public Optional<MutationFile> getMutationFileById(int id) {
		return Optional.ofNullable(mutationFileMapper.getMutationFileById(id));
	}
}
