# Spark Twitter Analysis
Analysis a twitter dataset with Spark and vizualize the results on a React dashboard.
There are three main modules:
1. `twitter-analytics-spark`: Spark program that processes a batch of tweets and stores useful data in MongoDB.
2. `twitter-analytics-webapp`: SpringBoot Restful API that serves data previsouly analysed and manages users.
3. `twitter-analytics-frontend`: React app that consumes the above API and displays it to the user.

## Running
* In `twitter-analytics-webapp` run `./gradlew build bootRun`.
* In `twitter-analytics-spark` run `sbt compile "runMain com.twitteranalytics.spark.twitter.TwitterSparkRDDMongoDB"` [Optional].
* In `twitter-analytics-frontend` run `npm start`.

## Dashboard
![image](https://user-images.githubusercontent.com/9336586/52723047-f3e3c780-2f93-11e9-8503-b6f1d25b2b13.png)
