# 🐇 RabbitMQ en Spring Boot - Conceptos y Patrones Corporativos

Este repositorio es una guía práctica y acumulativa sobre cómo implementar una arquitectura orientada a eventos usando **RabbitMQ** y **Spring Boot (Java 17+)**. Contiene desde los fundamentos de enrutamiento hasta patrones avanzados de resiliencia y tolerancia a fallos utilizados en entornos de producción.

## 📚 Glosario de Términos Base
*   **Producer (Productor):** El microservicio que envía el mensaje.
*   **Exchange (Intercambiador):** El "cartero" de RabbitMQ. Recibe el mensaje del productor y decide a qué cola(s) enviarlo según sus reglas.
*   **Queue (Cola):** El búfer donde se almacenan los mensajes hasta que son consumidos.
*   **Binding (Enlace):** La regla matemática que une un Exchange con una Queue.
*   **Routing Key (Clave de enrutamiento):** La etiqueta o dirección que usa el Exchange para saber por qué Binding enviar el mensaje.
*   **ACK (Acknowledge):** Confirmación positiva. Le dice a RabbitMQ: *"Ya procesé el mensaje, bórralo de la cola"*.
*   **NACK (Negative Acknowledge):** Confirmación negativa. Le dice a RabbitMQ: *"Fallé procesando esto. Devuélvelo a la cola o bótalo"*.

---

## 🏗️ Estructura del Proyecto (Paquetes)

Cada paquete en este proyecto representa un concepto arquitectónico aislado y funcional.

### 1. `fanout` (Patrón Broadcast / Pub-Sub)
*   **Concepto:** El `FanoutExchange` ignora por completo el *Routing Key*. Su única regla es: "Copia este mensaje y envíalo a **todas** las colas que estén enlazadas a mí".
*   **Casos de uso:** Notificaciones globales, limpiar cachés en múltiples servidores a la vez.

### 2. `direct` (Enrutamiento Estricto)
*   **Concepto:** El `DirectExchange` exige una coincidencia exacta. El mensaje solo va a la cola si su *Routing Key* es idéntico al del *Binding*.
*   **Casos de uso:** Separar tareas por niveles de prioridad (ej. `error`, `info`, `warning`) o por área (ej. `pagos`, `inventario`).

### 3. `topic` (Enrutamiento Inteligente / Patrones)
*   **Concepto:** El `TopicExchange` evalúa el *Routing Key* basándose en palabras separadas por puntos (ej. `latam.argentina.ventas`).
*   **Parámetros / Comodines:**
    *   `*` (Asterisco): Sustituye exactamente **una** palabra (ej. `*.argentina.*`).
    *   `#` (Numeral): Sustituye **cero o más** palabras (ej. `latam.#`).

### 4. `dlq` (Dead Letter Queue / Hospital de Mensajes)
*   **Concepto:** Una red de seguridad. Cuando un mensaje es rechazado por un consumidor (porque está corrupto o malformado), en lugar de perderse, se redirige a una cola de cuarentena (la DLQ) para ser analizado manualmente por un desarrollador.
*   **Configuración Clave:** Al declarar la cola principal, se le pasan `Arguments` nativos:
    *   `x-dead-letter-exchange`: A dónde enviar el mensaje si falla.
    *   `x-dead-letter-routing-key`: Con qué etiqueta enrutarlo en su caída.
*   **Activación:** Se activa cuando el consumidor lanza un rechazo manual: `channel.basicNack(tag, false, false)` (el último `false` significa `requeue = false`, es decir, "no lo vuelvas a encolar").

### 5. `retry` (Reintentos Automáticos - Backoff Exponencial)
*   **Concepto:** Protección contra fallos temporales (ej. caída de base de datos de 5 segundos). Spring Boot intercepta las excepciones del código y vuelve a inyectar el mensaje automáticamente.
*   **Parámetros:**
    *   `ackMode = "AUTO"`: Se usa en el `@RabbitListener` para cederle el control de los ACKs/NACKs a Spring.
    *   *Initial Interval / Multiplier / Max Attempts*: Configurados en el `yaml`. Permite esperas crecientes (2s, 4s, 8s) en lugar de bombardear un servicio caído. Cuando se agotan los intentos, Spring lanza un NACK definitivo que expulsa el mensaje a la DLQ.

### 6. `qos` (Quality of Service / Prefetch Count)
*   **Concepto:** Protección de memoria RAM (*Fair Dispatch*). Evita que RabbitMQ inunde a un consumidor lento enviándole 10,000 mensajes de golpe.
*   **Parámetro `prefetch: 1`:** Le dice a RabbitMQ: *"Entrégame solo 1 mensaje a la vez. No me mandes el siguiente hasta que no te envíe el ACK del actual"*. Permite un balanceo de carga real entre múltiples trabajadores.

### 7. `json` (Serialización de Objetos / POJOs)
*   **Concepto:** Traducción automática. Los sistemas reales no envían `Strings`, envían JSON.
*   **Clase Clave:** `JacksonJsonMessageConverter`. Un `@Bean` que intercepta los envíos y recepciones.
    *   *Productor:* Toma un objeto Java (ej. `Pedido`) y lo convierte a texto JSON.
    *   *Consumidor:* Atrapa el texto JSON de la red y reconstruye el objeto Java usando su **constructor vacío** y **setters**.

### 8. `confirms` (Publisher Confirms & Returns)
*   **Concepto:** Confiabilidad para el Productor. Evita enviar mensajes "a ciegas". Obliga a RabbitMQ a emitir recibos de entrega.
*   **Clases / Callbacks Clave:**
    *   `ConfirmCallback`: Confirma si el mensaje llegó físicamente al `Exchange` (o si hubo un error de red).
    *   `ReturnsCallback`: Confirma si el mensaje llegó al Exchange, pero el Exchange lo devolvió porque no encontró ninguna cola válida para la ruta solicitada.

---

## ⚙️ Configuraciones Estándar Corporativas (`application.yaml`)

Esta es la configuración definitiva para un entorno de producción, combinando todos los escudos de protección:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    
    # 1. PUBLISHER CONFIRMS (Confiabilidad en la emisión)
    publisher-confirm-type: correlated  # Exige recibo de llegada al Exchange
    publisher-returns: true             # Exige devolución si no encuentra ruta a una cola
    
    listener:
      simple:
        # 2. MODO DE CONFIRMACIÓN (Confiabilidad en la recepción)
        # 'manual': Obliga al dev a escribir channel.basicAck(). (Útil para flujos hiper-críticos).
        # 'auto': Spring manda el ACK al terminar el método, o NACK si hay Exception.
        acknowledge-mode: manual  # Se puede sobreescribir localmente con @RabbitListener(ackMode="AUTO")
        
        # 3. GESTIÓN DE MEMORIA
        prefetch: 1  # Fair Dispatch: Procesa 1 mensaje a la vez por consumidor.
        
        # 4. TOLERANCIA A FALLOS TEMP (Exponential Backoff)
        retry:
          enabled: true
          max-attempts: 3           # Intentos totales antes de enviarlo a la DLQ
          initial-interval: 2000ms  # Espera 2 segundos en el primer fallo
          multiplier: 2.0           # Duplica el tiempo en cada intento (2s, 4s, 8s...)
          max-interval: 10000ms     # Tope máximo de espera entre intentos
```
