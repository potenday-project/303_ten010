import './config.js'
import express from 'express'
import cors from 'cors'
import logger from 'morgan'

import error from "./middlewares/error.js";
import user from './routes/user.js'
import image from './routes/image.js'
import chatgpt from './routes/chatgpt.js'
import gallery from './routes/gallery.js'

// dotenv.config()
const app = express();

app.use(express.json());
app.use(logger('dev'));
app.use(cors());
app.use(error)

app.use('/user', user)
app.use('/image', image)
app.use('/chatgpt', chatgpt)
app.use('/gallery', gallery)

export default app