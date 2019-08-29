use ws::{Handler, Message, Request, Response, Result, Sender, CloseCode, WebSocket, Handshake};
use std::{fs, fmt};
use ws::util::Token;
use std::mem::replace;

pub struct Client {
    pub out: Sender,
  //  count: Rc<Cell<u32>>,
}

/*pub struct Sender = ws::Sender{
token: Token(),
channel: (),
connection_id: 0
}
*/

#[derive(Serialize, Deserialize, Debug)]
struct Location {
    unique_id: String,
    lat: f64,
    lon: f64,
    speed: f64,
    free_op: bool
}

#[derive(Serialize, Deserialize, Debug)]
struct SQLModel {
    trx_id: String,
    sql: String,
    updated: bool
}


#[derive(Debug)]
pub struct Truck {
    pub socket: Sender,
    pub unique_id: String
}


use std::default::Default;
use crate::{clients, Task, inspect, AppAction, Confirmation};
use core::borrow::Borrow;
use serde::de::Unexpected::Str;

/*
#[derive(Debug)]
pub struct Clients {
    pub client: Vec<Truck>
}

impl Clients {
    pub fn new() -> Self { Clients { client: Vec::new() } }

    pub fn insert(&mut self, t: Truck) {
        self.client.push(t)
    }
}

*/

impl Handler for Client {

    fn on_open(&mut self, _: Handshake) -> Result<()> {
        let truck = Truck {
            socket: self.out.clone(),
            unique_id: "".to_string()
        };


        println!("before opening: {}", clients.lock().unwrap().len());
        clients.lock().unwrap().push(truck);
        println!("before opening: {}", clients.lock().unwrap().len());
        println!("called {:#?}", clients.lock().unwrap());
      //  unsafe { clients }.insert(truck);
      //  println!("sockect: {:#?}", unsafe { clients });
       // CLIENTS.push(truck);
        // We have a new connection, so we increment the connection counter
       // Ok(self.count.set(self.count.get() + 1))
        Ok(())
    }

    fn on_message(&mut self, msg: Message) -> Result<()> {
        println!("Server got message '{}'. ", msg);

        println!("before msg: {:#?}", clients.lock().unwrap());


        match msg {
            Message::Text(msg) => {

            if let Ok(loc) = serde_json::from_str::<Location>(&msg) {
                println!("Location!");
                //  println!("socket:{}, id = {:?}", self.out.connection_id(), &deserialize.unique_id);
                self.out.broadcast(msg.clone());
                clients.lock().unwrap().iter_mut()
                    .filter(|client| client.socket == self.out)
                    .for_each(|s| s.unique_id = loc.unique_id.clone());

             //   println!("after msg: {:#?}", clients.lock().unwrap());

            } else if let Ok(sql) = serde_json::from_str::<SQLModel>(&msg) {
                println!("Sql!",);
                println!("Transaction: {}", &sql.trx_id);


                let command = serde_json::to_string(&Confirmation {
                    command: inspect(AppAction::Confirmation),
                    trx_id: sql.trx_id.clone(),
                    updated: true
                }).unwrap();

                self.out.send(command.clone());

            } else {
                    println!("Unrecognized");
            };
                Ok(())
            },
            Message::Binary(bin) => {
                Ok(())
            }
        }
    }

    fn on_close(&mut self, code: CloseCode, reason: &str) {
        println!("before closing: {}", clients.lock().unwrap().len());
        clients.lock().unwrap().retain(|client| client.socket != self.out);
        println!("after closing: {}", clients.lock().unwrap().len());

       // CLIENTS.retain(
       //     |truck| truck.socket.connection_id() != self.out.connection_id());
       // println!("trucks: {:#?}", CLIENTS);
        match code {
            CloseCode::Normal => println!("The client is done with the connection."),
            CloseCode::Away   => println!("The client is leaving the site."),
            _ => println!("The client encountered an error: {}", reason),
        }
    }

    fn on_request(&mut self, req: &Request) -> Result<(Response)> {
        match req.resource() {
            "/ws" => Response::from_request(req),
            // Create a custom response
            "/" => {
                let filename = "index.html";
                let contents = fs::read_to_string(filename).expect("Something went wrong reading the file");
                Ok(Response::new(200, "OK", contents.as_bytes().to_vec()))
            }
            _ => Ok(Response::new(404, "Not Found", b"404 - Not Found".to_vec())),
        }
    }
}