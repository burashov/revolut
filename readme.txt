	Money Transfer Application Demo 
			for revolut.com

Build:
	mvn clean install

Run:
	mvn exec:java

Wadl:
	http://localhost:8080/revolut/application.wadl

Examples:
	Create profile
	http://localhost:8080/revolut/profile/create/USD/100.1

	Get profile
	http://localhost:8080/revolut/profile/get/12efca51-93a7-4bd3-9a69-9315689930d0

	Transfer money:
	http://localhost:8080/revolut/profile/transfer/95d05331-1111-4545-9cd8-f607258af236/675ee014-5f61-4278-8cc9-bab395857eab/USD/1


(c) Iliya Burashov, 2016.
