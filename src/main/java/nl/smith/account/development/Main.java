package nl.smith.account.development;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public abstract class Main {

	public static void main(String[] args) throws IOException {

		PDDocument document = PDDocument.load(new File("/tmp/mozilla_mark0/test.pdf"));
		if (!document.isEncrypted()) {
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			System.out.println("Text:" + text);
		}
		document.close();

	}

}
