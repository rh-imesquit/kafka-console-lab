




Obter credenciais

```
$ oc get secret kafka-console-user -n kafka -o jsonpath='{.data.password}' | base64 -d
```

```
$ oc get secret app-user -n kafka -o jsonpath='{.data.password}' | base64 -d
```



```
$ curl -X POST <ROUTE>/orders   -H "Content-Type: application/json"   -d '{
    "customerId": "cliente-123",
    "items": ["item1", "item2", "item3"]
  }'
```