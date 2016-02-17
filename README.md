Survata Android SDK
====================

# Requirements #

1. The SDK supports api 14 and above.

# Setup #
1.  if you are using Maven in your project, the jar is available on [Maven Central](http://search.maven.org/#artifactdetails%7Ccom.amplitude%7Candroid-sdk%7C2.5.0%7Cjar) using the following configuration in your pom.xml:

    ```
    <dependency>
      <groupId>com.survata</groupId>
      <artifactId>survata-sdk</artifactId>
      <version>1.0.1</version>
    </dependency>
    ```

  Or if you are using gradle in your project, include in your build.gradle file:

    ```
    compile 'com.survata:survata-sdk:1.0.1'
    ```
2.  import com.survata.Survey at the top:

    ```java
    import com.survata.Survey;
    ```
3.  initialize Survata:

    ```java
    private Survey mSurvey = new Survey();
    ```
4.  check survey

    ```java
    mSurvey.create(this,
                    contentName,
                    publisherUuid,
                    postalCode,
                    new Survey.SurveyAvailabilityListener() {
                        @Override
                        public void onSurveyAvailable(Survey.SurveyAvailability surveyAvailability) {
                            if (surveyAvailability == Survey.SurveyAvailability.AVAILABILITY) {
                                // do something
                            } else {

                            }
                        }
                    });
    ```
5.  show survey

    ```java
    mSurvey.createSurveyWall(MainActivity.this, publisher, brand, explainer, new Survey.SurveyStatusListener() {
                @Override
                public void onResult(Survey.SurveyResult surveyResult) {
                    if (surveyResult == Survey.SurveyResult.COMPLETED) {
                        // do something
                    }
                }
            });
    ```