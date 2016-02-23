Survata Android SDK
====================

# Requirements #

- Android SDK v14

# Setup #
1.  Add in your build.gradle file:

    ```
    compile 'com.survata.android:library:1.0.1'
    ```
2.  Here is a brief demo to bind Survey to a button:

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