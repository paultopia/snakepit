import pika
import json

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='py2clj')
channel.queue_declare(queue='clj2py')


channel.queue_declare(queue='jsonpy2clj')
channel.queue_declare(queue='jsonclj2py')


def plaintext_callback(ch, method, properties, body):
    mess = "Python Received %r" % body
    print(mess)
    channel.basic_publish(exchange='', routing_key='py2clj', body='Hello from Python!')
    print(" Python sent 'Hello from Python!'")

def json_callback(ch, method, properties, body):
    mess = json.loads(body.decode("utf-8"))
    print("python got: " + str(mess))
    response = [x + 1 for x in mess]
    print("python sez: " + str(response))
    channel.basic_publish(exchange='', routing_key='jsonpy2clj', body=json.dumps(response))

channel.basic_consume(plaintext_callback,
                      queue='clj2py',
                      no_ack=True)

channel.basic_consume(json_callback,
                      queue='jsonclj2py',
                      no_ack=True)




print('Python Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
