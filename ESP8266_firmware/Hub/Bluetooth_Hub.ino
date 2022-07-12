/*********
    плата отправитель
*********/
#include <<span class="crayon-s">esp_now.h></span>;
#include <span class="crayon-p"><WiFi.h>;</span>

// Список MAC - адесов плат-приемников
uint8_t broadcastAddress1[] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
uint8_t broadcastAddress2[] = {0xFF, , , , , };


// Тестовая структура
typedef struct test_struct {
  int x;
  int y;
} test_struct;

test_struct test;

//
unsigned long lastTime = 0;       //????
unsigned long timerDelay = 2000;  // таймер задержки

// сообщение, если данные отправлены
void OnDataSent(uint8_t *mac_addr, uint8_t sendStatus) {
  char macStr[18];
  Serial.print("Packet to:");
  snprintf(macStr, sizeof(macStr), "%02x:%02x:%02x:%02x:%02x:%02x",
         mac_addr[0], mac_addr[1], mac_addr[2], mac_addr[3], mac_addr[4], mac_addr[5]);
  Serial.print(macStr);
  Serial.print(" send status: ");
  if (sendStatus == 0){
    Serial.println("Delivery success");
  }
  else{
    Serial.println("Delivery fail");
  }
}
 
void setup() {
    Serial.begin(115200);
    
    WiFi.mode(WIFI_STA);
    
    if (esp_now_init() != ESP_OK) {
        Serial.println("Error initializing ESP-NOW");
        return;
    }
  
    esp_now_set_self_role(ESP_NOW_ROLE_CONTROLLER);
    //получаем состояние отправки пакета
    esp_now_register_send_cb(OnDataSent);
   
    // регистрируем платы в сети
    esp_now_add_peer(broadcastAddress1, ESP_NOW_ROLE_SLAVE, 1, NULL, 0);
    esp_now_add_peer(broadcastAddress2, ESP_NOW_ROLE_SLAVE, 1, NULL, 0);
}
void loop() {
if ((millis() - lastTime) > timerDelay) {
    // Set values to send
    test.x = random(1, 50);
    test.y = random(1, 50);

    // Отправляем сообщение
    esp_now_send(0, (uint8_t *) &test, sizeof(test));

    lastTime = millis();
  }
}