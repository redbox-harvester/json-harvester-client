package au.com.redboxresearchdata.harvester.json.transformer

import java.io.File
import java.io.IOException
import java.util.List
import java.util.Map

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.log4j.Logger
import org.springframework.integration.Message
import org.springframework.integration.annotation.Header
import org.springframework.integration.annotation.Payload
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.file.FileHeaders
import org.springframework.integration.support.MessageBuilder

import au.com.redboxresearchdata.types.TypeFactory

/*
 * Spring Integration Transformation Handler
 * 
 * @author Shilo Banihit
 * @since 1.0
 *
 */
class JsonTransformationHandler {

	private static final Logger log = Logger.getLogger(JsonTransformationHandler.class)
	
	@Transformer
	public Message<String> handleFile(final Message<File> inputMessage) {

		final File inputFile = inputMessage.getPayload()
		final String filename = inputFile.getName()
		final String fileExtension = FilenameUtils.getExtension(filename)

		final String inputAsString

		try {
			inputAsString = FileUtils.readFileToString(inputFile)
		} catch (IOException e) {
			throw new IllegalStateException(e)
		}

		final Message<String> message = MessageBuilder.withPayload(inputAsString)
					.setHeader(FileHeaders.FILENAME,      filename)
					.setHeader(FileHeaders.ORIGINAL_FILE, inputFile)
					.setHeader("file_size", inputFile.length())
					.setHeader("file_extension", fileExtension)
					.build()

		return message
	}
		
	@Transformer
	public Message<String> handleJdbc(@Payload List<Map> payload, @Header("type") String type) {		
		String data = TypeFactory.buildJsonStr(payload, type)
		if (log.isDebugEnabled()) {
			log.debug("JSON message payload: ${data}")
		}
		final Message<String> message = MessageBuilder.withPayload(data)
				.setHeader("type", type)
				.build()

		return message
	}
	
}
