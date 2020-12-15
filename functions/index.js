const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.nuevoPedido = functions.firestore
    .document('Pedidos/{IDpedido}')
    .onCreate(async (snap, context) => {
      const document = snap.data();
      if(document !== null && !(document.uid === "")){
          var nombre = document.nombre
          var payload = {
             notification: {
                title: 'Nuevo pedido!',
                body: 'Se ha recibido un pedido de '+ nombre +"."
             }
           };
           const alltokens = await admin.firestore().collection('TokensEMP').get();
           const tokens = [];
           alltokens.forEach((tokenDoc) => {
                tokens.push(tokenDoc.data().token);
           });
           if(tokens.length > 0){
              await admin.messaging().sendToDevice(tokens, payload);
              console.log(tokens)
           }
      }
      return true;
    });

exports.cambioPedido = functions.firestore
    .document('Pedidos/{IDpedido}')
    .onUpdate((change, context) => {
      const document = change.after.exists ? change.after.data() : null;
      const document2 = change.before.exists ? change.before.data() : null;
      if(document !== null && (!(document.estado === document2.estado) || !(document.precio === document2.precio)) && !(document.uid === "")){
          var nombre = document.nombre;
          var payload = {}
          if((document.estado === document2.estado)){
                 payload = {
                       notification: {
                          title: "Cambio de precio!",
                          body: "El nuevo precio de su pedido " +nombre+" es: " + document.precio+"€."
                        }
                 }
          }else{
                  payload = {
                       notification: {
                          title: "Cambio de estado!",
                          body: "Su pedido de " +nombre+" está " + document.estado+"."
                    }
                  }
          }
          console.log(payload)
          var userRef = admin.firestore().collection('TokensCLI').doc(document.uid);
          return userRef
              .get()
              .then(doc => {
                if (!doc.exists) {
                  throw new Error('No existe el usuario!');
                } else {
                console.log(doc.data().token)
                  admin.messaging().sendToDevice(doc.data().token, payload)
                  console.log(payload)
                  return true
                }
              })
              .catch(err => {
                console.log(err)
                return false;
              });
          }
          return false;
    });