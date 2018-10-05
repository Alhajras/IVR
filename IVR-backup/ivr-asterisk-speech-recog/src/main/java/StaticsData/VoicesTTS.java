package StaticsData;

import lombok.Getter;

@Getter
public enum VoicesTTS {

	GERMAN_FEMALE_1("bits1-hsmm"), // Female DE
	GERMAN_MALE_1("bits3-hsmm"), // Male DE // the best german voice

	ENGLISH_FEMALE_1("dfki-poppy-hsmm"), // Female USA
	ENGLISH_FEMALE_2("cmu-slt-hsmm"), // Female USA
	ENGLISH_FEMALE_3("dfki-prudence-hsmm"), // Female uk

	ENGLISH_MALE_1("dfki-spike-hsmm"), // male USA // best english voice
	ENGLISH_MALE_2("cmu-bdl-hsmm"); // Male USA

	private final String voice;

	private VoicesTTS(String voice) {
		this.voice = voice;
	}

}
