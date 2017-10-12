//Wifi
#include <ESP8266WiFi.h>

const char* ssid     = "SKY1DA94";
const char* password = "TXXXYABR";
const char* host = "example.com";

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
 
void setup(void) {
  delay(20);
pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output for ultrasonic
pinMode(echoPin, INPUT); // Sets the echoPin as an Input for ultrasonic
  Serial.begin(9600);

//Connecting to wifi
Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}
 
void loop(void) {
  uint8_t i;
  float average;
   
  // take N samples in a row, with a slight delay for an accurate thermistor reading
  for (i=0; i< NUMSAMPLES; i++) {
   samples[i] = analogRead(THERMISTORPIN);
   delay(10);
  }
 
  // average all the samples out of the thermistor reading
  average = 0;
  for (i=0; i< NUMSAMPLES; i++) {
     average += samples[i];
  }
  average /= NUMSAMPLES;
 
  Serial.print("Average analog reading "); 
  Serial.println(average);
 
  // convert the value to resistance
  average = 1023 / average - 1;
  average = SERIESRESISTOR / average;
  Serial.print("Thermistor resistance "); 
  Serial.println(average);

  //Convert the value to celcius 
  float steinhart;
  steinhart = average / THERMISTORNOMINAL;     // (R/Ro)
  steinhart = log(steinhart);                  // ln(R/Ro)
  steinhart /= BCOEFFICIENT;                   // 1/B * ln(R/Ro)
  steinhart += 1.0 / (TEMPERATURENOMINAL + 273.15); // + (1/To)
  steinhart = 1.0 / steinhart;                 // Invert
  steinhart -= 273.15;                         // convert to C

  Serial.print("Temperature "); 
  Serial.print(steinhart);
  Serial.println(" *C");

  //ULTRASONIC SENSOR READING

  // Clears the trigPin
digitalWrite(trigPin, LOW);
delayMicroseconds(2);

// Sets the trigPin on HIGH state for 10 micro seconds
digitalWrite(trigPin, HIGH);
delayMicroseconds(10);
digitalWrite(trigPin, LOW);

// Reads the echoPin, returns the sound wave travel time in microseconds
duration = pulseIn(echoPin, HIGH);


// Calculating the distance
distance= duration*0.034/2;
// Prints the distance on the Serial Monitor
Serial.print("Distance: ");
Serial.println(distance);
delay(1500);

  //Logging purposes
  if(steinhart > 30){
    Serial.println("TEMPERATURE OVER 30 degrees");
  }
  if(distance <10 ){
    Serial.println("sensor reading below 10");
  }
  
    Serial.println("===Sending request to server===");
delay(2500);
// Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort = 80;
  
if(steinhart > 30 && distance <10){
  //SEND A POST REQUEST INDICATING CAR SPOT IS OCCUPIED
  if (!client.connect("example.com", httpPort)) {
    Serial.println("connection failed");
    return;
  }
  Serial.print("connecting to ");
  Serial.println(host);
  
  

  // We now create a URI for the request
  String url = "/";
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
  }else{
    //CAR IS NOT OCCUPIED HENCE SEND POST INDICATING ITS FREE
    if (!client.connect("google.com", httpPort)) {
    Serial.println("connection failed");
    return;
  }
  Serial.print("connecting to ");
  Serial.println(host);
  
  

  // We now create a URI for the request
  String url = "/";
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
  while(client.available()){
    String line = client.readStringUntil('\r');
    Serial.print(line);
  }
    Serial.println();
  Serial.println("closing connection");
  
   
  delay(2000);





}



