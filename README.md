# ESP_RGBWLight
Проект для создания кастомного управления светодиоидной лентой с помощью ESP8266 через Android приложение
## Подключение и общение
Для общения нескольких esp между собой будет использоватлься Wi-Fi/Bluetooth подключение. 
Связь со смартфоном будет осуществлена через bluetooth подключение основного хаба.
Обмен сообщений будет осуществляться за основу протокол контроллера, взятого с Aliexpress. Благодарить стоит [Вот этого человека](https://github.com/arduino12/ble_rgb_led_strip_controller), разобравшего протокол. 
Для целий данного проекта он более чем подходит, но в будущем будет расширен для увеличения функционала.

## Протокол
### Разбор
В качестве примера приведем разбор следующего сообщения

#### Сообщение: `7e 07 05 03 00 ff 00 00 ef`.
| byte | description |
| - | - |
| `7e` | Начало команды |
| `07` | don't care (so I use 0 instad) |
| `05` | command id (this case set_color) |
| `03` | command sub-id (this case rgb color) |
| `00` | значение красного |
| `FF` | значение зеленого |
| `00` | значение голубого |
| `00` | don't care (so I use 0 instad) |
| `ef` | Конец команды |

### Тестирование
Для тестирования использовался [bluetooth-le-explorer](https://www.microsoft.com/en-us/p/bluetooth-le-explorer/9n0ztkf1qd98?activetab=pivot:overviewtab) приложение от Microsoft с открытым исходным кодом для отправки сообщений по bluetooth.

Пример MAC-адреса контроллера "ELK-BLEDOM" (be:ff:f0:01:04:a8),and write the hex command bytes to it's only writeable attribute: `0000fff0-0000-1000-8000-00805f9b34fb`.

### Контроллер имеет 2 состояния включения (use set_power command):
| states | description |
| - | - |
| `on` | контроллер показывает корректный режим|
| `off` | контроллер отключил ленту (текущий режим сохранен) |

### Контроллер обладал 5 режимами (use *mode* commands to change)
| modes | description |
| - | - |
| `mode_grayscale` | меняет оттенок серого от черного до полностью белого|
| `mode_temperature` | Устанавливает яркость и температуру(от холодного до теплого белого) |
| `mode_effect` | auto color change, can set patterns, brightness and speed |
| `mode_dynamic` | ? (maybe on-board mic is needed -mine just shows last color) |  
| `mode_rgb` | можно установить значения rgb и яркость |

### Контроллер обладал 5 режимами (use *mode* commands to change)
| Команда | Описание | Место в коде (X) | Диапазон |
| - | - | - | - |
| Смена яркости | Не работает при mode_effect: мигание градиента перехода? mode_grayscale  mode_dynamic   | `7e 00 01 XX 00 00 00 00 ef` | 0-100 (0x00-0x64)   |
| Скорость      | -                                                                                       | `7e 00 02 XX 00 00 00 00 ef` | 0-100 (0x00-0x64)   |
| -             | will show last grayscale color                                                          | `7e 00 03 00 01 00 00 00 ef` | -                   |
| Температура   | -                                                                                       | `7e 00 03 XX 02 00 00 00 ef` | 128-138 (0x80-0x8a) |
| Эффекты       | -                                                                                       | `7e 00 03 XX 03 00 00 00 ef` | `7e00038703000000ef` jump_rgb, `7e00039203000000ef` gradient_rg, `7e00039503000000ef` blink_rgbycmw   |
| Режим питания | -                                                                                       | `7e 00 04 XX 00 00 00 00 ef` | 0-1 (0x00-0x01)     |
| Полутон       | -                                                                                       | `7e 00 05 01 XX 00 00 00 ef` | 0-100 (0x00-0x64)   |
| - | - | - | - |
| - | - | - | - |
| - | - | - | - |
| - | - | - | - |
| - | - | - | - |
| - | - | - | - |
| - | - | - | - |
| - | - | - | - |

#### `set_rgb_pin_order(rgb_order):`
`7e 00 08 rgb_order 00 00 00 00 ef`  
`7e 00 08 01 00 00 00 00 ef` rgb  
`rgb_order`: 1:rgb 2:rbg 3:grb 4:gbr 5:brg 6:bgr  
Did not do anything on my controller.  


### Команды
##### `effect`:
| effect | description|
| - | - |
| `0x80` | r (red) |
| `0x81` | g (green) |
| `0x82` | b (blue) |
| `0x83` | y (yellow) |
| `0x84` | c (cyan) |
| `0x85` | m (magenta) |
| `0x86` | w (white) |
| `0x87` | jump_rgb |
| `0x88` | jump_rgbycmw |
| `0x89` | gradient_rgb |
| `0x8a` | gradient_rgbycmw |
| `0x8b` | gradient_r |
| `0x8c` | gradient_g |
| `0x8d` | gradient_b |
| `0x8e` | gradient_y |
| `0x8f` | gradient_c |
| `0x90` | gradient_m |
| `0x91` | gradient_w |
| `0x92` | gradient_rg |
| `0x93` | gradient_rb |
| `0x94` | gradient_gb |
| `0x95` | blink_rgbycmw |
| `0x96` | blink_r |
| `0x97` | blink_g |
| `0x98` | blink_b |
| `0x99` | blink_y |
| `0x9a` | blink_c |
| `0x9b` | blink_m |
| `0x9c` | blink_w |

#### `set_mode_dynamic(val):`
`7e 00 03 val 04 00 00 00 00 ef`  
`7e00030004000000ef` val 0  
Did not do anything on my controller (just freeze the current color).  

#### `set_color_for_temperature_mode(температура):`
`7e 00 05 02 temperature 00 00 00 ef`
`temperature`: 0-100 (0x00-0x64)

#### `set_color_for_rgb_mode(r, g, b):`
`7e 00 05 03 r g b 00 ef` 
`r, g, b`: 0-255 (0x00-0xff)

#### `set_val_for_dynamic_mode(val):`
`7e 00 06 val 00 00 00 00 ef`
Did not do anything on my controller.

#### `set_sensitivity_for_dynamic_mode(sensitivity):`
`7e 00 07 sensitivity 00 00 00 00 ef`  
`7e00070000000000ef` sensitivity 0  
Did not do anything on my controller.  

#### `set_rgb_pin_order(c1 c2 c3):`
`7e 00 81 c1 c2 c3 00 00 ef`  
`7e00810102030000ef` rgb  
`7e00810302010000ef` bgr  
`c1 c2 c3`: (1-3) use each value once!  
