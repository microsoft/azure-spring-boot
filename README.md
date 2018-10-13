[![Travis CI](https://travis-ci.org/Microsoft/azure-spring-boot.svg?branch=master)](https://travis-ci.org/Microsoft/azure-spring-boot)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/af0qeprdv3g9ox07/branch/master?svg=true)](https://ci.appveyor.com/project/yungez/azure-spring-boot)
[![codecov](https://codecov.io/gh/Microsoft/azure-spring-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/Microsoft/azure-spring-boot)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/Microsoft/azure-spring-boot/blob/master/LICENSE)
[![Gitter](https://badges.gitter.im/Microsoft/spring-on-azure.svg)](https://gitter.im/Microsoft/spring-on-azure)

# Azure Spring Boot

### Introduction

This repo is for Spring Boot Starters of Azure services. It helps Spring Boot developers to adopt Azure services.

### Support Spring Boot
This repository supports both Spring Boot 1.5.x and 2.0.x. Please read [this document](https://github.com/Microsoft/azure-spring-boot/wiki/Spring-Boot-dependency-version-management) for branch mapping.

### Prerequisites
- JDK 1.8 and above
- [Maven](http://maven.apache.org/) 3.0 and above

### Usage

Below starters are available. You can find them in [Maven Central Repository](https://search.maven.org/).
The first three starters are also available in [Spring Initializr](http://start.spring.io/). 

Starter Name | Version
---|---
[azure-active-directory-spring-boot-starter](azure-spring-boot-starters/azure-active-directory-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-active-directory-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-active-directory-spring-boot-starter%22)
[azure-storage-spring-boot-starter](azure-spring-boot-starters/azure-storage-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-storage-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-storage-spring-boot-starter%22)
[azure-keyvault-secrets-spring-boot-starter](azure-spring-boot-starters/azure-keyvault-secrets-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-keyvault-secrets-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-keyvault-secrets-spring-boot-starter%22)
[azure-cosmosdb-spring-boot-starter](azure-spring-boot-starters/azure-cosmosdb-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-documentdb-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-documentdb-spring-boot-starter%22)
[azure-mediaservices-spring-boot-starter](azure-spring-boot-starters/azure-mediaservices-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-mediaservices-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-mediaservices-spring-boot-starter%22)
[azure-servicebus-spring-boot-starter](azure-spring-boot-starters/azure-servicebus-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-servicebus-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-servicebus-spring-boot-starter%22)
[spring-data-gremlin-boot-starter](azure-spring-boot-starters/spring-data-gremlin-boot-starter) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/spring-data-gremlin-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22spring-data-gremlin-boot-starter%22)
[azure-sqlserver-spring-boot-starter](azure-spring-boot-starters/azure-sqlserver-spring-boot-starter/README.md) | [![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-sqlserver-spring-boot-starter.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-sqlserver-spring-boot-starter%22)




### कैसे बनाएं और योगदान करें
यह परियोजना योगदान और सुझावों का स्वागत करती है। अधिकतर योगदानों के लिए आपको एक योगदानकर्ता लाइसेंस अनुबंध (सीएलए) से सहमत होने की आवश्यकता होती है कि आपको यह अधिकार है कि आपको अपने योगदान का उपयोग करने का अधिकार है, और वास्तव में ऐसा करने का अधिकार है। विवरण के लिए, https://cla.microsoft.com पर जाएं।

स्रोत या योगदान से निर्माण करने के लिए कृपया [निर्देश यहां] (./ HowToContribute.md) का पालन करें।

### अन्य लेख
विशिष्ट स्टार्टर्स के उपयोग पर अधिक जानने के लिए आप नीचे लेखों की जांच कर सकते हैं।

[Azure Cosmos DB DocumentDB API के साथ स्प्रिंग बूट स्टार्टर का उपयोग कैसे करें] (https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-java-spring-boot-starter-with-cosmos- डाटाबेस)

### फाइलिंग मुद्दे

अगर आपको कोई बग मिलती है, तो कृपया एक समस्या दर्ज करें [यहां] (https://github.com/Microsoft/azure-spring-boot/issues/new)।

एक नई सुविधा या परिवर्तन किए जाने वाले सुझावों का सुझाव देने के लिए, एक समस्या को उसी तरह दर्ज करें जैसे आप एक बग के लिए करेंगे।

आप समुदाय संचालित [! [गिटर] (https://badges.gitter.im/Microsoft/spring-on-azure.svg) में भाग ले सकते हैं] (https://gitter.im/Microsoft/spring-on-azure)

### अनुरोध खींचें

पुल अनुरोधों का स्वागत है। अपना खुद का पुल अनुरोध खोलने के लिए, [यहां] क्लिक करें (https://github.com/Microsoft/azure-spring-boot/compare)। पुल अनुरोध बनाते समय, सुनिश्चित करें कि आप कांटा और शाखा को इंगित कर रहे हैं कि आपके परिवर्तन किए गए थे।

### आचार संहिता

इस परियोजना ने [माइक्रोसॉफ्ट ओपन सोर्स आचार संहिता] (https://opensource.microsoft.com/codeofconduct/) अपनाया है। अधिक जानकारी के लिए [आचार संहिता पूछे जाने वाले प्रश्न] (https://opensource.microsoft.com/codeofconduct/faq/) देखें या किसी भी अतिरिक्त प्रश्न के साथ [opencode@microsoft.com] (mailto: opencode@microsoft.com) से संपर्क करें या टिप्पणियाँ।
