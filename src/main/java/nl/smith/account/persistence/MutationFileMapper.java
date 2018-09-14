package nl.smith.account.persistence;

import nl.smith.account.domain.MutationFile;

public interface MutationFileMapper {

	void persist(MutationFile mutationFile);

	MutationFile getMutationFileByAbsoluteFilePath(String absoluteFilePath);

	MutationFile getMutationFileById(long id);

}
