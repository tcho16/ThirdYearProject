//Wifi
#include <ESP8266WiFi.h>

/*Lenovo P2 for ssid when demoing. SKY1DA94 when at home*/
const char* ssid     = "Lenovo P2";
const char* password = "TXXXYABR";

/*Host changes everytime when connecting to a different network. Be wary*/
const char* host = "192.168.43.49";

//THERMISTOR BELOW
// which analog pin to connect the thermistor
#define THERMISTORPIN A0
// resistance at 25 degrees C
#define THERMISTORNOMINAL 10000
// temp. for nominal resistance (almost always 25 C)
#define TEMPERATURENOMINAL 25
// how many samples to take and average, more takes longer
// but is more 'smooth'
#define NUMSAMPLES 5
// The beta coefficient of the thermistor (usually 3000-4000)
#define BCOEFFICIENT 3950
// the value of the 'other' resistor
#define SERIESRESISTOR 10000


// defines pins numbers for ultrasonic
const int trigPin = 2;  //D4
const int echoPin = 0;  //D3
// defines variables
long duration;
int distance;

uint16_t samples[NUMSAMPLES];

void setUpUltrasonicPins() {
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output for ultrasonic
  pinMode(echoPin, INPUT); // Sets the echoPin as an Input for ultrasonic
}

void connectToWifi() {
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  /*REMEMEBER TO CHANGE WHEN IN UNI
  WiFi.begin(ssid, password);
  */
  WiFi.begin(ssid);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void setup(void) {
  delay(20);

  //Setup pins for ultrasonic sensor
  setUpUltrasonicPins();

  //Connecting to wifi
  connectToWifi();

  Serial.begin(9600);
}

float averageOfThermistorReadings() {
  uint8_t i;
  float average;

  // take N samples in a row, with a slight delay for an accurate thermistor reading
  for (i = 0; i < NUMSAMPLES; i++) {
    samples[i] = analogRead(THERMISTORPIN);
    delay(10);
  }
  average = 0;
  for (i = 0; i < NUMSAMPLES; i++) {
    average += samples[i];
  }
  average /= NUMSAMPLES;
  return average;
}

float steinhartConversion(float average) {
  float steinhart;
  steinhart = average / THERMISTORNOMINAL;     // (R/Ro)
  steinhart = log(steinhart);                  // ln(R/Ro)
  steinhart /= BCOEFFICIENT;                   // 1/B * ln(R/Ro)
  steinhart += 1.0 / (TEMPERATURENOMINAL + 273.15); // + (1/To)
  steinhart = 1.0 / steinhart;                 // Invert
  steinhart -= 273.15;                         // convert to C
  return steinhart;
}

float ultrasonicCalculation() {
  // Clears the trigPin
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);

  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Reads the echoPin, returns the sound wave travel time in microseconds
  return duration = pulseIn(echoPin, HIGH);
}

void loop(void) {
  // average all the samples out of the thermistor reading
  float average = averageOfThermistorReadings();
  Serial.print("Average analog reading ");
  Serial.println(average);

  // convert the value to resistance
  average = 1023 / average - 1;
  average = SERIESRESISTOR / average;
  Serial.print("Thermistor resistance ");
  Serial.println(average);

  //Convert the value to celcius
  float steinhart = steinhartConversion(average);
  Serial.print("Temperature ");
  Serial.print(steinhart);
  Serial.println(" *C");

  // Calculating the distance
  distance = ultrasonicCalculation() * 0.034 / 2;

  // Prints the distance on the Serial Monitor
  Serial.print("Distance: ");
  Serial.println(distance);
  delay(1500);

  //Logging purposes
  if (steinhart > 30) {
    Serial.println("TEMPERATURE OVER 30 degrees");
  }
  if (distance < 10 ) {
    Serial.println("sensor reading below 10");
  }

  Serial.println("===Sending request to server===");
  delay(2500);
  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort = 8080;

  if (steinhart > 22 && distance < 10) {
    //SEND A REQUEST INDICATING CAR SPOT IS OCCUPIED
    if (!client.connect(host, httpPort)) {
      Serial.println("connection failed");
      return;
    }
    Serial.print("connecting to ");
    Serial.println(host);



    // We now create a URI for the request
    String url = "/posttodb?id=59&longitude=2.330469&latitude=46.446416&status=1";
    Serial.print("Requesting URL: ");
    Serial.println(url);

    // This will send the request to the server
    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
                 "Host: " + host + "\r\n" +
                 "Connection: close\r\n\r\n");
    unsigned long timeout = millis();
    while (client.available() == 0) {
      if (millis() - timeout > 5000) {
        Serial.println(">>> Client Timeout !");
        client.stop();
        return;
      }
    }
  } else {
    //CAR IS NOT OCCUPIED HENCE SEND POST INDICATING ITS FREE
    if (!client.connect(host, httpPort)) {
      Serial.println("connection failed");
      return;
    }
    Serial.print("connecting to ");
    Serial.println(host);



    // We now create a URI for the request 51.518220, -0.141136
    String url = "/posttodb?id=59&longitude=2.330469&latitude=46.446416&status=0";
    Serial.print("Requesting URL: ");
    Serial.println(url);

    // This will send the request to the server
    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
                 "Host: " + host + "\r\n" +
                 "Connection: close\r\n\r\n");
    unsigned long timeout = millis();
    while (client.available() == 0) {
      if (millis() - timeout > 5000) {
        Serial.println(">>> Client Timeout !");
        client.stop();
        return;
      }
    }
  }

  // Read all the lines of the reply from server and print them to Serial
  while (client.available()) {
    String line = client.readStringUntil('\r');
    Serial.print(line);
  }
  Serial.println();
  Serial.println("closing connection");


  delay(2000);

}



