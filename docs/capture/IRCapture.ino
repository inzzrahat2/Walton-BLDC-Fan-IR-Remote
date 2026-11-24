// Captures codes from the fan's original remote so they can be compared with FanButton.kt.
// Hardware: Arduino Uno/Nano (or ESP32) + TSOP38238 / VS1838B IR receiver.
//   receiver OUT -> pin 2, VCC -> 5V (3.3V on ESP32), GND -> GND
// Library: "IRremote" by Armin Joachimsmeyer, v4.x (Arduino Library Manager).
// Open Serial Monitor at 115200, point the remote at the receiver, press each button once.

#include <IRremote.hpp>

const uint8_t IR_RECEIVE_PIN = 2;

void setup() {
  Serial.begin(115200);
  IrReceiver.begin(IR_RECEIVE_PIN, ENABLE_LED_FEEDBACK);
  Serial.println(F("Ready. Press a button on the fan remote."));
}

void loop() {
  if (!IrReceiver.decode()) return;

  // e.g. "Protocol=NEC Address=0x0 Command=0x45 Raw-Data=0xBA45FF00 32 bits LSB first"
  // NEC: compare Address and Command with FanButton.kt (Walton uses the 16-bit address 0x7ED8).
  IrReceiver.printIRResultShort(&Serial);

  // Fallback for non-NEC remotes: print the raw timings as a C array.
  if (IrReceiver.decodedIRData.protocol != NEC) {
    IrReceiver.compensateAndPrintIRResultAsCArray(&Serial, true);
  }
  Serial.println();
  IrReceiver.resume();
}
