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
pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output for ultrasonic
pinMode(echoPin, INPUT); // Sets the echoPin as an Input for ultrasonic
  Serial.begin(9600);
 // analogReference(EXTERNAL);
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
  if(steinhart > 30 && distance <10){
    Serial.println("Send request to server");
  }
   
  delay(4000);
}



