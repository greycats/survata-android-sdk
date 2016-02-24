Survata Android SDK
====================

# Requirements #

- Android SDK v14

# How to create aar and upload to jcenter #

1.  Register for an account on bintray.com

2.  Package aar and upload

    [bintray-release](https://github.com/novoda/bintray-release), it's provide a super duper easy way to release your Android and other artifacts to bintray.
    
    Refer the steps in [README.md](https://github.com/novoda/bintray-release/blob/master/README.md). 
    
    * modify `publish` closure in `build.gradle`
    
    ```groovy
         publish {
             userOrg = ''
             groupId = ''
             artifactId = ''
             publishVersion = ''
             desc = ''
             website = ''
         }
    ```
    
    * use the task `bintrayUpload` to publish (make sure you build the project first!):
    
    ```bash
    $ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
    ```

3.  Sync bintray user repository to jcenter

    * You now have your own Maven Repository on Bintray which is ready to be uploaded the library to.
    
    [![ScreenShot](step1.png)](https://github.com/greycats/survata-android-sdk/blob/development/step1.png)


    * Nothing to do but click Send
    
    [![ScreenShot](step2.png)](https://github.com/greycats/survata-android-sdk/blob/development/step2.png)


    * It is pretty easy to sync your library to jcenter. Just go to the web interface and simply click at "Add to JCenter".
    Nothing we can do now but wait for 2-3 hours to let bintray team approves our request. Once sync request is approved, you will receive an email informing you the change. 
    
    [![ScreenShot](step3.png)](https://github.com/greycats/survata-android-sdk/blob/development/step3.png)

# Setup #
1.  Add in your build.gradle file:

    ```
    compile 'com.survata.android:library:1.0.2'
    ```
2.  Please check out [demo app](https://github.com/greycats/survata-android-demo) for a real-life demo.
    Here is a brief demo to bind Survey to a button:

    ```java
     public void checkSurvey() {
            Context context = getContext();
            SurveyDebugOption option = new SurveyDebugOption(publisherId);
    
            mSurvey = new Survey(option);
            mSurvey.create(getActivity(),
                    new Survey.SurveyAvailabilityListener() {
                        @Override
                        public void onSurveyAvailable(Survey.SurveyAvailability surveyAvailability) {
                           
                            if (surveyAvailability == Survey.SurveyAvailability.AVAILABILITY) {
                                // do something
                            }
                        }
                    });
        }
        
     private void showSurvey() {                
            mSurvey.createSurveyWall(getActivity(), new Survey.SurveyStatusListener() {
                    @Override
                    public void onResult(Survey.SurveyResult surveyResult) {
                               
                        if (surveyResult == Survey.SurveyResult.COMPLETED) {
                            // do something
                        }
                    }
                });
            }
    ```