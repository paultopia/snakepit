This is a quick and dirty demonstration of how Python and Clojure can communicate with one another using rabbitmq.  

The use case for me is that I'm writing a webapp in Clojure, and I want it to be backed by some machine learning, and of course Python has the best libraries for that kind of stuff.  So, when I want to fit a model, I'm just going to send the data to python and let it send the result back to Clojure when it's done.

To run on your machine: 

1.  Clone this repo.

2.  Install dependencies.  For python, you'll need pika, `pip install pika`.  You'll also need rabbitmq. `brew install rabbitmq` or the apt-get/whev equivalent.  If you install via homebrew on mac, be aware that [it lands in a place that isn't automatically on PATH for some silly reason](https://stackoverflow.com/questions/23050120/rabbitmq-command-doesnt-exist).  

3.  Fire up rabbitmq with `/usr/local/sbin/rabbitmq-server`

4.  Open up a new terminal window and fire up the python script with `python talktoclojure.py`

5.  Open up a third terminal window and `lein run`.

You should see Clojure send a message to Python which will print on the Python terminal, and then see Python send a reply to Clojure which will print on the Clojure terminal.

6.  To cleanly shut down, control-c out of the python terminal, and run `/usr/local/sbin/rabbitmqctl stop` to [get rabbitmq to shut down](https://stackoverflow.com/questions/20615765/how-to-stop-rabbitmq-servers) --- keyboard interrupts won't do the trick for the last one. Clojure will shut itself down.

You can also just use the shell script talk.sh that will handle the details for you.

Created using the basic leiningen app template, mostly following [this tutorial on the Clojure-side](http://clojurerabbitmq.info/articles/getting_started.html) and [this tutorial on the Python-side](https://www.rabbitmq.com/tutorials/tutorial-one-python.html), so, like, almost none of the code is actually mine, but any residual rights that I may have in, I dunno, the act of putting the two tutorials together or something, is hereby committed to the public domain. 

Other useful info: [this github issue](https://github.com/MassTransit/MassTransit/issues/370) explains why the default needs to be changed on the clojure-side to `:auto-delete false`. And [this](https://www.cloudamqp.com/blog/2015-05-18-part1-rabbitmq-for-beginners-what-is-rabbitmq.html) is a very nice general explainer for how rabbitmq works. I also looked at [this](http://bernhardwenzel.com/articles/how-to-build-a-message-driven-microservice-application/), though didn't get too much from it.

