import pika
connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='py2clj')
channel.queue_declare(queue='clj2py')



def callback(ch, method, properties, body):
    mess = "Python Received %r" % body
    print(mess)
    channel.basic_publish(exchange='', routing_key='py2clj', body='Hello from Python!')
    print(" Python sent 'Hello from Python!'")
 
channel.basic_consume(callback,
                      queue='clj2py',
                      no_ack=True)

print('Python Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
