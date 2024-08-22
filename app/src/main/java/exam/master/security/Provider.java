package exam.master.security;

import lombok.Getter;

@Getter
public class Provider {
  private final String registrationId;

  public static final Provider GOOGLE = new Provider("google");
  public static final Provider FACEBOOK = new Provider("facebook");
  public static final Provider GITHUB = new Provider("github");
  public static final Provider KAKAO = new Provider("kakao");
  public static final Provider NAVER = new Provider("naver");

  private Provider(String registrationId) {
    this.registrationId = registrationId;
  }

  public static Provider from(String registrationId) {
    switch (registrationId.toLowerCase()) {
      case "google":
        return GOOGLE;
      case "facebook":
        return FACEBOOK;
      case "github":
        return GITHUB;
      case "kakao":
        return KAKAO;
      case "naver":
        return NAVER;
      default:
        throw new UnsupportedOperationException("Unsupported provider: " + registrationId);
    }
  }
}

