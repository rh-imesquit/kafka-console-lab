# Red Hat Streams for Apache Kafka Lab

This lab aims to practically demonstrate the functionalities of the Kafka Console integrated with Red Hat Streams for Apache Kafka 2.9, enabling real-time visualization and monitoring of the main components of a Kafka cluster.

During the exercise, a simple and instructional scenario will be implemented, in which a Producer application will send order records to a Kafka topic called orders, while a Consumer application will be responsible for consuming these records and displaying them via log. The main focus will be on using the Kafka Console to explore the following features:

* Visualization of topics and their messages

* Monitoring of consumer groups

* Checking operational metrics of the cluster

* Tracking detailed information about partitions, offsets, and replication status

This environment has been prepared to facilitate the understanding of essential observability concepts in Kafka and to demonstrate how the Kafka Console can be a powerful tool in supporting the operation and diagnosis of Kafka clusters in corporate environments.

### <h2 style="color: #e5b449;">What is</h2>

**Red Hat Streams for Apache Kafka** is an enterprise distribution of Apache Kafka offered by Red Hat, aimed at developing event-driven applications in Kubernetes and OpenShift environments. It provides a reliable, secure, and integrated platform for real-time data streaming, with full Red Hat support, and includes operators to simplify the installation, configuration, management, and scalability of Kafka clusters in hybrid or on-premises cloud environments.

**Red Hat Streams for Apache Kafka Console** is a web interface included with Red Hat Streams for Apache Kafka that allows intuitive visualization and monitoring of the main components of a Kafka cluster. With it, you can inspect topics, track real-time messages, view consumer groups, check operational metrics, and access detailed information about partitions, offsets, and replication status. The console enhances Kafka observability and management, being especially useful in OpenShift environments, where it integrates natively with the cluster.

### <h2 style="color: #e5b449;">Technologies and versions used in this tutorial</h2>

| Component                                   | Version |
|---------------------------------------------|---------|
| Red Hat OpenShift Container Platform        | 4.19    |
| Red Hat Streams for Apache Kafka            | 3.0.1   |
| Red Hat Streams for Apache Kafka Console    | 3.0.1   |
| Quarkus Community (Java 17)                 | 3.20    |


### <h2 style="color: #e5b449;">How-to install and configure Red Hat Streams for Apache Kafka</h2>

First, create a new project called kafka. To do this, in the left-hand menu, select Home > Projects and click the Create Project button.

![Creating kafka project](./images/kafka/01%20-%20Creating%20the%20Kafka%20project.png)

In the window that opens, fill in the Name field with kafka and click the Create button.

Next, in the left-hand menu, go to Operators > OperatorHub. On this screen, search for "kafka" and select the operator Streams for Apache Kafka provided by Red Hat.

An information screen about the operator will appear. Click the Install button.

![Installing Kafka Operator](./images/kafka/02%20-%20Installing%20Kafka%20Operator.png)

In the form that appears, keep the default values and click Install.
Once the installation is successfully completed, the message "Installed operator: ready to use" will be displayed. At this point, click the View Operator button. The Operator details screen will appear.

![Installing Kafka Operator](./images/kafka/03%20-%20Installing%20Kafka%20Operator.png)

Now let's start creating the Kafka CRs located in the [infra/01-kafka/](./infra/01-kafka/) directory of this repository. From here on, we'll proceed via terminal using the oc CLI.

```
$ cd /infra/01-kafka
```

Log in to OpenShift using the command:

```
$ oc login -u <USER> -p <PASSWORD> <HOST>:6443
```

If you're not in the kafka namespace, switch to it by running:

```
$ oc project kafka
```

Now we will need to create our Kafka Node Pools resource.

Kafka Node Pools are a feature that allows you to define groups of Kafka brokers with different configurations within the same Kafka cluster.

Apply the KafkaNodePool resource by executing:

```
$ oc apply -f kafka-node-pool.yaml -n kafka
```

Verify that two node pools — broker and controller — were created in the KafkaNodePools tab.

![Creating KafkaNodePool resource](./images/kafka/04%20-%20Creating%20KafkaNodePool%20resource.png)

Let's apply the following JMX Prometheus Exporter configuration used by Strimzi to expose Kafka metrics in Prometheus format. Once properly configured in the Kafka resource, the operator injects the JMX Exporter into the broker, and Prometheus collects the metrics. These metrics can then feed Grafana dashboards and similar tools to enhance observability capabilities.

Apply the ConfigMap with metrics resource by executing:

```
$ oc apply -f kafka-metrics-cm.yaml -n kafka
```

Now, apply the Kafka resource, which is responsible for defining, deploying, and automatically managing an Apache Kafka cluster (and its components, such as ZooKeeper or KRaft, depending on the mode) within an OpenShift environment.

```
$ oc apply -f kafka.yaml -n kafka
```

Wait a moment, as it may take some time for all Kafka cluster resources to be provisioned. Finally, verify that the resource was created in the Kafka tab with the Ready status.

![Creating Kafka resource](./images/kafka/05%20-%20Creating%20Kafka%20resource.png)


In the next step, we need to create the Kafka users required for the producer and consumer applications, as well as the user for accessing the Kafka Console — all with their respective permissions assigned through ACL (Access Control List) configuration.

Apply the KafkaUser resources by executing:

```
$ oc apply -f kafka-users.yaml -n kafka
```

Verify that the users were created in the KafkaUser tab with the Ready status.

![Creating KafkaUser resources](images/kafka/06%20-%20Creating%20KafkaUser%20resources.png)


Finally, let’s define the KafkaTopic, which is the resource responsible for managing Kafka topics in a declarative and automated way within OpenShift.

Apply the KafkaTopic resource by running:

```
$ oc apply -f kafka-topic.yaml -n kafka
```

Verify that the resource was created in the KafkaTopic tab with the Ready status.
![Creating KafkaTopic resources](./images/kafka/07%20-%20Creating%20KafkaTopic%20resource.png)

Check if all pods in the kafka namespace are in Running status. Go to the side menu and select Workloads > Pods.

![Kafka pods running](./images/kafka/08%20-%20Kafka%20pods%20running.png)

We’ve completed the Kafka installation part in the OpenShift cluster. Proceed to the next step.

### <h2 style="color: #e5b449;">How-to install and configure Prometheus to collect and export Kafka metrics</h2>

We will now prepare the environment to enable metrics collection and processing through Prometheus.

**Prometheus** is a system monitoring and observability tool widely used in DevOps environments, cloud infrastructure, and distributed applications (such as microservices). It is an open-source system that collects and stores time-series metrics — that is, data that changes over time, such as CPU usage, memory consumption, number of HTTP requests, latency, and so on.

In the left-hand menu, go to Operators > OperatorHub. On this screen, search for "prometheus" and select the operator Prometheus Operator provided by Red Hat.

An information screen about the operator will appear. Click the Install button.

![Installing Prometheus Operator](./images/prometheus/01%20-%20Installing%20Prometheus%20Operator.png)

Once the installation is successfully completed, the message "Installed operator: ready to use" will be displayed.

In your terminal, navigate to the directory containing the YAML files that configure Prometheus in the cluster.

```
$ cd /infra/02-metrics
```

Next, we’ll apply the Prometheus-related YAML files in the [infra/02-metrics/](./infra/02-metrics/) directory of this repository, that contain the rules, pod monitors, integrations with the Alertmanager, and other related configurations.

```
$ oc apply -f 01-prometheus-install/
```

```
$ oc apply -f 02-prometheus-additional-properties/
```

```
$ oc apply -f 03-prometheus-alertmanager-config/
```

After applying the resources, check if all Prometheus-related Pods are already running and have the Running status.

![Installing Prometheus Operator](./images/prometheus/02%20-%20Prometheus%20pods%20running.png)

We’ve completed the Prometheus installation and configuration part in the OpenShift cluster. Proceed to the next step.


### <h2 style="color: #e5b449;">How-to install and configure Red Hat Streams for Apache Kafka Console</h2>

In the left-hand menu, go to Operators > OperatorHub. On the screen that appears, search for Kafka and select the operator Streams for Apache Kafka Console (provided by Red Hat).

An information screen about the operator will be displayed. Click the Install button.

![Installing Kafka Console Operator](./images/kafka/09%20-%20Installing%20Kafka%20Console%20Operator.png)

In the installation form, keep the default values and click Install again.
Once the installation is complete, the message "Installed operator: ready to use" will be displayed. Then, click the View Operator button, which will take you to the Operator Details screen.

![Installing Kafka Console Operator](./images/kafka/10%20-%20Installing%20Kafka%20Console%20Operator.png)

Go back to the directory where the YAML files related to Kafka are located.

```
$ cd /infra/01-kafka
```

Next, apply the Console resource by running the appropriate command.

```
$ oc apply -f kafka-console.yaml -n kafka
```

Verify that the resource was created and that the Console tab shows the Ready status.

![Installing Console resource](./images/kafka/11%20-%20Creating%20Console%20resource.png)

After instantiating the resource in the kafka project, a route will be automatically created for the console. To view it, go to the left-hand menu and select Networking > Routes. This route will redirect you to the Kafka Console login screen.

![Getting console route](./images/kafka/12%20-%20Getting%20console%20route.png)

The credentials are available in a Secret that was automatically generated when the KafkaUser resource was created. Since the user console-user was created to access the console, you can retrieve the password by running the corresponding command.

```
$ oc get secret console-user -n kafka -o jsonpath='{.data.password}' | base64 -d
```

Enter the username and password, then click the Access button.

![Apache Kafka Console login page](./images/kafka/13%20-%20Apache%20Kafka%20Console%20login%20page.png)

From this point on, you will be connected to the Kafka Console dashboard, which will be explored in more detail in the next steps.

![Apache Kafka Console dashboard](./images/kafka/14%20-%20Apache%20Kafka%20Console%20dashboard.png)

We’ve completed the Kafka Console installation part in the OpenShift cluster. Proceed to the next step.


### <h2 style="color: #e5b449;">How-to deploy producer and consumer applications</h2>

Before getting started, review the applications located in the [apps/](./apps/) directory. We will deploy them through the OpenShift console.

First, let's create the app project using the CLI.

```
$ oc new-project app
```

Next, we’ll apply the Secret resource that will store the credentials for the app-user. This secret is located in the [infra/03-application/](./infra/03-application/) directory.

Go to the directory where the YAML file containing the mentioned secret is located.

```
$ cd /infra/03-application
```

Note that the secret requires the app-user password. You can retrieve it by running the following command:

```
$ oc get secret app-user -n kafka -o jsonpath='{.data.password}' | base64 -d
```

Replace the credential value in the YAML file and apply it.

```
$ oc apply -f kafka-app-user-credentials-secret.yaml -n app
```

In the OpenShift console, click the “+” button in the top-right corner of the screen to open the import menu, and select Import from Git.

![Add screen](./images/applications/01%20-%20Import%20from%20Git.png)

In the form, fill in the fields according to the producer application information.

![Producer application import from Git](./images/applications/02%20-%20Producer%20application%20import%20from%20Git.png)

You can use the reference table to assist in creating the application.

| Field             | Value                                             |
|-------------------|---------------------------------------------------|
| Git Repo URL      | https://github.com/rh-imesquit/kafka-console-lab  |
| Context dir       | /apps/webstore-producer                           |
| Application name  | kafka-console-lab-app                             |
| Name              | webstore-producer                                 |

In the Deploy section, include the environment variables. To do this, click the Show advanced Deployment options link and fill in the variables as shown below.

![Producer application import from Git](./images/applications/03%20-%20Producer%20application%20import%20from%20Git.png)

| Environment Var             | Value                                                     |
|-----------------------------|-----------------------------------------------------------|
| KAFKA_CLUSTER_ENDPOINT      | Obtain it from the kafka-cluster-kafka-bootstrap service with port 9092 |
| KAFKA_USER                  | Obtain it from the kafka-app-user-credentials secret.                   |
| KAFKA_PASSWORD              | Obtain it from the kafka-app-user-credentials secret.                   |


Finally, click the Create button. This will start the Source-to-Image (S2I) build process in OpenShift, which should be awaited until completion.

Once finished, the producer application pod should have a Running status.

Now, repeat the same process for the consumer application.

| Field             | Value                                             |
|-------------------|---------------------------------------------------|
| Git Repo URL      | https://github.com/rh-imesquit/kafka-console-lab  |
| Context dir       | /apps/webstore-consumer                           |
| Application name  | kafka-console-lab-app                             |
| Name              | webstore-consumer                                 |

At environment vars use

| Environment Var             | Value                                                     |
|-----------------------------|-----------------------------------------------------------|
| KAFKA_CLUSTER_ENDPOINT      | Obtain it from the kafka-cluster-kafka-bootstrap service with port 9092   |
| KAFKA_USER                  | Obtain it from the kafka-app-user-credentials secret.                     |
| KAFKA_PASSWORD              | Obtain it from the kafka-app-user-credentials secret.                     |

After filling out the corresponding form, click the Create button again.

When the build process is complete and the pod reaches Running status, both applications will be ready for pub/sub message communication.

On the information screen for the producer application, locate the generated route on the right-hand side and copy the provided URL. We'll use this route to send messages to Kafka.

![Producer and Consumer pods running](./images/applications/04%20-%20Producer%20and%20Consumer%20pods%20running.png)

Here are some example requests to be made. Run the curl command for the POST /orders endpoint, as shown in the example below. Don’t forget to replace the exemple route with the one created for the producer application on OpenShift:

```
$ curl -X POST https://webstore-producer-app.apps.cluster-dtmzr.dtmzr.sandbox2930.opentlc.com/orders \
-H "Content-Type: application/json" \
-d '{"customerId": "ianmesquita","items": ["iPhone 15", "Dell laptop 17", "JBL Airpods"]}'
```

```
$ curl -X POST https://webstore-producer-app.apps.cluster-dtmzr.dtmzr.sandbox2930.opentlc.com/orders \
-H "Content-Type: application/json" \
-d '{"customerId": "ianmesquita","items": ["LG Monitor", "Logitech Mouse", "JBL Airpods"]}'
```

```
$ curl -X POST https://webstore-producer-app.apps.cluster-dtmzr.dtmzr.sandbox2930.opentlc.com/orders \
-H "Content-Type: application/json" \
-d '{"customerId": "ianmesquita","items": ["Mousepad", "Microphone", "Logitec Webcam"]}'
```

```
curl -X POST https://webstore-producer-app.apps.cluster-dtmzr.dtmzr.sandbox2930.opentlc.com/orders \
-H "Content-Type: application/json" \
-d '{"customerId": "ianmesquita","items": ["Work Table", "Water Cooler", "Logitec Pen"]}'
```

![Producer logs](./images/applications/05%20-%20POST%20request.png)

Finally, view the logs of the producer and consumer applications, confirming the message exchange between them.

![Producer logs](./images/applications/06%20-%20Producer%20logs.png)

![Consumer logs](./images/applications/07%20-%20Consumer%20logs.png)

In the left-hand menu of the Kafka Console, select the Topics option. Access the orders topic to view the data stored in the broker related to this topic.

The **Messages** tab displays a chronological list of messages for a topic.

![Topic message tab](./images/kafka/15%20-%20Topic%20message%20tab.png)

The **Partitions** tab shows list of partitions belonging to a topic. As defined in the kafka-topic.yaml file, there should be 6 partitions and 3 replicas.

![Topic partitions tab](./images/kafka/16%20-%20Topic%20partitions%20tab.png)

The **Consumer Groups** tab displays a list of consumer groups associated with a topic.

![Topic consumer groups tab](./images/kafka/17%20-%20Topic%20consumer%20groups%20tab.png)

The **Configuration** tab presents a list of configuration values for the topic, organized in a key:value format.

![Topic configuration tab](./images/kafka/18%20-%20Topic%20configuration%20tab.png)

On the Kafka nodes screen, you can view the storage utilization metrics for each node that makes up the Kafka cluster, as well as other information such as which node pool the node belongs to and the number of replicas.

![Kafka Nodes screen](./images/kafka/19%20-%20Kafka%20nodes%20screen.png)

Finally, notice that after sending a few records to the topic, all the metric charts start displaying Kafka usage data.

![Apache Kafka Console dashboard with metrics](./images/kafka/20%20-%20Apache%20Kafka%20Console%20dashboard%20with%20metrics.png)

Great job! We have completed our lab.

### <h2 style="color: #e5b449;">Conclusion</h2>

After going through the entire journey of this lab, you are now familiar with the convenience of the Kafka Console within Red Hat Streams for Apache Kafka.

In this lab, you not only installed and configured a Kafka cluster on OpenShift but also explored the tool that greatly simplifies the life of anyone who needs to visualize, monitor, and understand what's happening inside Kafka: the Kafka Console.

You learned how to:

* Install the Kafka Console via Operator

* Access the visual interface and log in securely

* Navigate through topics, messages, partitions, consumer groups, and configurations with just a few clicks

* View, in real time, the message exchange between producer and consumer applications

The idea here was to show that, with the Kafka Console, you no longer need to rely solely on the terminal or depend on logs to understand what's going on in the cluster. With it, it's much easier to investigate, test, and validate applications that use Kafka — all in a visual, intuitive way, directly within OpenShift.

### <h2 style="color: #e5b449;">References</h2>

- [Red Hat Streams for Apache Kafka 3.0 Documentation](https://docs.redhat.com/pt-br/documentation/red_hat_streams_for_apache_kafka/3.0)
- [Red Hat Streams for Apache Kafka Console 3.0 Documentation](https://docs.redhat.com/pt-br/documentation/red_hat_streams_for_apache_kafka/3.0/html/using_the_streams_for_apache_kafka_console/index)
- [Quarkus Documentation - Apache Kafka Reference Guide](https://quarkus.io/guides/kafka)

