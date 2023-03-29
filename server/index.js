import app from './app.js'
import connect from "./models/connect.js";

(async () => {
    const port = process.env.PORT || 3000
    await connect()
    app.listen(port, () => {
        console.log(`app listening on port ${port}`)
    })
})()
