package StaticsData;

import lombok.Getter;

@Getter
public enum Language {
	GERMAN("de"), ENGLISH("en");

	private String lang;

	private Language(String lang) {
		this.lang = lang;
	}
}
