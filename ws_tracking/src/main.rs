#![feature(proc_macro_hygiene, decl_macro)]
#![feature(const_vec_new)]

#[macro_use] extern crate rocket;
use rocket::config::{Config, Environment};
#[macro_use] extern crate rocket_contrib;
#[macro_use] extern crate lazy_static;

use std::sync::Mutex;

use std::thread;
use std::time::Duration;

mod socket;
use crate::socket::{Client, Truck};
use rocket_contrib::templates::Template;
use std::collections::HashMap;
use rocket_contrib::serve::StaticFiles;
use std::rc::Rc;
use std::cell::Cell;

#[macro_use]
extern crate serde_derive;
extern crate serde;
extern crate serde_json;

lazy_static! {
    static ref clients: Mutex<Vec<Truck>> = Mutex::new(vec![]);
}

#[get("/")]
fn index() -> Template {
    let context = HashMap::<String, String>::new();
    Template::render("index", context)
}

use rocket::request::{Form, FormError, FormDataError};
use rocket_contrib::json::{Json, JsonValue, JsonError};

#[derive(Serialize, Deserialize, Debug, FromForm)]
struct Task {
    command: String,
    truck_id: String,
    task: String,
    details: String,
    lon: f32,
    lat: f32,
    address: String,
    datePicker: String,
    timePicker: String,
    status: String,
    customer: String,
    mobile: usize
}

#[derive(Serialize, Deserialize)]
struct Confirmation {
    command: String,
    trx_id: String,
    updated: bool
}

enum AppAction {
    Task,
    Confirmation,
    Notification
}

fn inspect(appAction: AppAction) -> String {
    match appAction {
        AppAction::Task => format!("Task"),
        AppAction::Confirmation => format!("Confirmation"),
        AppAction::Notification => format!("Notification"),
        _ => String::new()
    }
}


#[derive(Debug, Serialize, Deserialize)]
struct Message {
    checkbox: bool,
    number: usize,
    radio: String,
    password: String,
    text_area: String,
    select: String,
}

#[post("/", data = "<task>")]
fn submit_task(task: Result<Json<Task>, JsonError>) -> JsonValue {
    match task {
        Ok(value) => {
            println!("task: {:?}", value.0);  // value is json{Task{}}, value.0 is Task{}
            let command = serde_json::to_string(&value.0).unwrap();

            clients.lock().unwrap().iter_mut()
                .filter(|client| client.unique_id == value.truck_id)
                .for_each(|client| {
                    println!("received msg from client: {:#?}", client);
                    client.socket.send(command.clone());
                }) ;
            json!({
                "status": "Success",
                "reason": format!("Received Json: {:?}", value.0)
            })
        },
        Err(JsonError::Io(e)) => json!({
                "status": "error",
                "reason": format!("I/O Error: {:?}", e)
            }),
        Err(JsonError::Parse(raw, e)) => json!({
                "status": "error",
                "reason": format!("{:?} is not valid JSON: {:?}", raw, e)
            })
    }
}

fn main() {
    println!("Hello, world!");
    let count = Rc::new(Cell::new(0));
    let mut ws = ws::WebSocket::new(
        |out| Client { out }).unwrap();

    thread::spawn(|| { ws.listen("127.0.0.1:8080").unwrap(); });
/*    let h = thread::spawn(|| {
        ws.listen("127.0.0.1:8080").unwrap();
       // thread::sleep(Duration::new(500, 0));
       // panic!("boom");
    });

    let r = h.join();
    handle(r);
*/
   // rocket::ignite().mount("/", routes![index]).launch();
    let config = Config::build(Environment::Production)
        .address("127.0.0.1")
        .port(8081)
        .finalize().unwrap();

    rocket::custom(config)     // replaces calls to rocket::ignite() & Rocket.toml
        .mount("/static", StaticFiles::from(concat!(env!("CARGO_MANIFEST_DIR"), "/static")))
        .mount("/", routes![index, submit_task])
        .attach(Template::fairing())
   //     .attach(AdHoc::on_attach("Assets Config", |rocket| {
   //         let assets_dir = rocket.config()
   //             .get_str("assets_dir")
   //             .unwrap_or("assets/")
   //             .to_string();

   //         Ok(rocket.manage(AssetsDir(assets_dir)))
   //     }))
        .launch();

}

fn handle(r: thread::Result<()>) {
    match r {
        Ok(r) => println!("All is well! {:?}", r),
        Err(e) => {
            if let Some(e) = e.downcast_ref::<&'static str>() {
                println!("Got an error: {}", e);
            } else {
                println!("Got an unknown error: {:?}", e);
            }
        }
    }
}
