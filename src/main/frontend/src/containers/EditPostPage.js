import React, {useEffect, useState} from "react";
import '../App.css';
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Item from "../utils/Item";
import {colors} from "../utils/colors";
import axios from "axios";
import FormControl from "@mui/material/FormControl";
import TextField from "@mui/material/TextField";
import FormHelperText from "@mui/material/FormHelperText";
import PostAddRoundedIcon from '@mui/icons-material/PostAddRounded';
import {MenuItem, Select, Stack} from "@mui/material";
import Grid from "@mui/material/Grid";

function EditPostPage() {
    // Render on start
    useEffect(() => {
        fetchCategorias();
        const cookie = window.localStorage.getItem("user");
        if(cookie) {
            let user = JSON.parse(cookie);
            setUser(user);
        };
    }, []);
    const [categorias, setCategorias] = useState([])
    const reader = new FileReader();

    const [user, setUser] = useState({});
    const [image, setImage] = useState([]);
    const [titulo, setTitulo] = useState("");
    const [desc, setDesc] = useState("");
    const [cat, setCat] = useState(0);

    const fetchCategorias = async () => {
        try {
            const response = await axios.get('http://localhost:8080/categoria/all');
            const data = response.data;
            setCategorias(data);
        } catch (error) {
            alert("Error obteniendo categorías: "+error);
        }
    }

    const handleChange = async (e) => {
        switch (e.target.name) {
            case "titulo": setTitulo(e.target.value); break;
            case "descripcion": setDesc(e.target.value); break;
            case "categoria": setCat(e.target.value); break;
            case "imagen": setImage(e.target.files[0]); break;
            default: break;
        }
    }
    const fileToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => {
                const base64String = reader.result.split(',')[1];
                resolve(base64String);
            };
            reader.onerror = (error) => {
                reject(error);
            };
            reader.readAsDataURL(file);
        });
    };

    const editPost = async (event) => {
        event.preventDefault();
        let userID = user.idUser;
        let publicacionID = window.localStorage.getItem("pubId");
        var formdata = new FormData();
        formdata.append("titulo", titulo);
        formdata.append("descripcion", desc);
        formdata.append("categoria_ID", cat);
        formdata.append("imagen", await fileToBase64(image));
        formdata.append("userID", userID);
        formdata.append("id", publicacionID);
        console.log('PUBLICACION: ', {titulo, desc, userID, cat});
        axios.put('http://localhost:8080/publicacion/update', formdata, {
            headers: {
                'Content-Type': 'application/json'
            }}
        )
            .then(function (response) {
                alert(response.data);
                let href = window.location.href;
                href = href.substring(0, href.lastIndexOf('/'));
                window.location.replace(href+"/home");
            })
            .catch(function (error) {
                console.log(error.response.data);
                alert(error.response.data);
            });
    }

    return (
        <React.Fragment>
            <Grid container spacing={2} className="FullWidthPage">
                <Grid item xs={12}>
                    <Typography variant="subtitle1">Editar Publicación</Typography>
                </Grid>
                <Grid item xs={4}>
                    <Stack spacing={2} direction="column">
                        <FormControl>
                            <TextField onChange={(event)=> {handleChange(event)}}
                                       placeholder="Título" type="text" variant="outlined" name="titulo" className="AddPostForm"
                            />
                            <FormHelperText id="titulo-text">Ingrese el título de su publicación</FormHelperText>
                        </FormControl>
                        <FormControl>
                            <TextField onChange={(event)=> {handleChange(event)}} className="AddPostForm"
                                       placeholder="Descripción" multiline={true} rows={4} type="text" variant="outlined" name="descripcion"
                            />
                            <FormHelperText id="descripcion-text">Describa el producto publicado</FormHelperText>
                        </FormControl>
                    </Stack>
                </Grid>
                <Grid item xs={4}>
                    <Stack spacing={2} direction="column">
                        <FormControl>
                            <Select
                                className="AddPostForm"
                                onChange={(event)=> {handleChange(event)}}
                                name="categoria" placeholder="Categoría"
                            >
                                { categorias.map((categoria) => (
                                    <MenuItem value={categoria.id}>{categoria.nombre}</MenuItem>
                                ))}
                            </Select>
                            <FormHelperText id="categoria-text">Seleccione la categoría del producto publicado</FormHelperText>
                        </FormControl>
                        <FormControl>
                            <TextField onChange={(event)=> {handleChange(event)}}
                                       placeholder="Imagen" type="file" variant="outlined" name="imagen" className="AddPostForm"
                            />
                            <FormHelperText id="descripcion-text">Agregue una foto del producto publicado</FormHelperText>
                        </FormControl>
                        <Button variant="contained" color="success" startIcon={<PostAddRoundedIcon color="primary"/>}
                                onClick={editPost}>
                            <Typography variant="button">Publicar</Typography>
                        </Button>
                    </Stack>
                </Grid>
            </Grid>
        </React.Fragment>
    )
}

export default EditPostPage;