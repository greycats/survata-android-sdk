Survata Android SDK
====================

# Requirements #

- Android SDK v14

# Setup #
1.  if you are using Maven in your project, the jar is available on [Maven Central](http://search.maven.org/#artifactdetails%7Ccom.amplitude%7Candroid-sdk%7C2.5.0%7Cjar) using the following configuration in your pom.xml:

    ```
    <dependency>
      <groupId>com.survata.android</groupId>
      <artifactId>library</artifactId>
      <version>1.0.0</version>
    </dependency>
    ```

  Or if you are using gradle in your project, include in your build.gradle file:

    ```
    compile 'com.survata.android:library:1.0.0'
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
    mSurvey.setPublisherUuid(publisherUuid);
    // optional
    mSurvey.setPostalCode(postalCode);
    mSurvey.create(this,
                    contentName,
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
    SurveyOption surveyOption = new SurveyOption(brand, explainer, preview);
    mSurvey.createSurveyWall(MainActivity.this, publisher, surveyOption, new Survey.SurveyStatusListener() {
                @Override
                public void onResult(Survey.SurveyResult surveyResult) {
                    if (surveyResult == Survey.SurveyResult.COMPLETED) {
                        // do something
                    }
                }
            });
    ```