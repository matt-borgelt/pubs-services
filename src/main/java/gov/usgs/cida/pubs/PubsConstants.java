package gov.usgs.cida.pubs;

import org.springframework.http.MediaType;

public final class PubsConstants {

	public static final String DEFAULT_ENCODING = "UTF-8";


	public static final String MEDIA_TYPE_RSS_VALUE = MediaType.TEXT_XML_VALUE;
	public static final String MEDIA_TYPE_CSV_VALUE = "text/csv";
	public static final String MEDIA_TYPE_TSV_VALUE = "text/tab-separated-values";
	public static final String MEDIA_TYPE_XLSX_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	
	public static final MediaType MEDIA_TYPE_CSV = new MediaType("text", "csv");
	public static final MediaType MEDIA_TYPE_TSV = new MediaType("text", "tab-separated-values");
	public static final MediaType MEDIA_TYPE_XLSX = new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	public static final MediaType MEDIA_TYPE_RSS = MediaType.TEXT_XML;
	
	public static final String MEDIA_TYPE_CSV_EXTENSION = "csv";
	public static final String MEDIA_TYPE_TSV_EXTENSION = "tsv";
	public static final String MEDIA_TYPE_XLSX_EXTENSION = "xlsx";
	public static final String MEDIA_TYPE_XML_EXTENSION = "xml";
	public static final String MEDIA_TYPE_JSON_EXTENSION = "json";
	public static final String MEDIA_TYPE_RSS_EXTENSION = "rss";
	
	public static final String CONTENT_PARAMETER_NAME = "mimeType";
	public static final String ACCEPT_HEADER = "Accept";
	
	
	public static final String SPACES_OR_NUMBER_REGEX = "^ *\\d*$";
	public static final String FOUR_DIGIT_REGEX = "^\\d{4}$";
	public static final String SEARCH_TERMS_SPLIT_REGEX = "[^a-zA-Z\\d]";

	/** The default username for anonymous access. */
	public static final String ANONYMOUS_USER = "anonymous";

	public static final String DOI_PREFIX = "10.3133";

	public static final Integer DEFAULT_LOCK_TIMEOUT_HOURS = 3;
	
	public static final String NOT_IMPLEMENTED = "NOT IMPLEMENTED.";
	
	private PubsConstants() {
	}

}
