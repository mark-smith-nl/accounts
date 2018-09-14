package nl.smith.account.domain;

import javax.validation.constraints.NotNull;

public class MutationFile {

	private Integer id;

	@NotNull
	private String absoluteFilePath;

	@NotNull
	private byte[] fileBytes;

	@NotNull
	private int checksum;

	// Used by MyBatis
	private MutationFile() {
		System.out.println("Yesssssssssssssssssssssss");
	}

	public MutationFile(@NotNull String absoluteFilePath, @NotNull byte[] fileBytes, @NotNull int checksum) {
		this();
		this.absoluteFilePath = absoluteFilePath;
		this.fileBytes = fileBytes;
		this.checksum = checksum;
	}

	public Integer getId() {
		return id;
	}

	// Used by MyBatis
	@SuppressWarnings("unused")
	private void setId(Integer id) {
		this.id = id;
	}

	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public int getChecksum() {
		return checksum;
	}

}
